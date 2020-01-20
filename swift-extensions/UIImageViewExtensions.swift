import UIKit
import TRIKOT_FRAMEWORK_NAME

extension UIImageView {
    public var metaImage: MetaImage? {
        get { return trikotMetaView() }
        set(value) {
            metaView = value
            if value != nil {
                let cancellableManagerProvider = CancellableManagerProvider()

                restoreContentMode()
                downloadImageIfNeeded(cancellableManagerProvider.cancelPreviousAndCreate())

                let sizeObservationCancellation = KeyValueObservationHolder(self.observe(\UIImageView.bounds, options: [.old, .new]) {[weak self] (_, change) in
                    if change.newValue?.size != change.oldValue?.size { self?.downloadImageIfNeeded(cancellableManagerProvider.cancelPreviousAndCreate()) }
                })

                trikotInternalPublisherCancellableManager.add(cancellable: sizeObservationCancellation)
                trikotInternalPublisherCancellableManager.add(cancellable: cancellableManagerProvider)
            }
        }
    }

    func downloadImageIfNeeded(_ cancellableManager: CancellableManager) {
        guard let metaImage = metaImage else { return }
        observeImageFlow(cancellableManager: cancellableManager, imageFlowPublisher: metaImage.imageFlow(width: Int32(frame.width) * 2, height: Int32(frame.height) * 2))
    }

    func observeImageFlow(cancellableManager: CancellableManager, imageFlowPublisher: Publisher) {
        guard let metaImage = metaImage else { return }

        DispatchQueue.main.async {[weak self] in
            let cancellableManagerProvider = CancellableManagerProvider()
            cancellableManager.add(cancellable: cancellableManagerProvider)

            self?.observe(cancellableManager: cancellableManager, publisher: metaImage.imageFlow(width: Int32(self?.frame.width ?? 0) * 2, height: Int32(self?.frame.height ?? 0) * 2)) {[weak self] (imageFlow: ImageFlow) in
                self?.doLoadImageFlow(cancellableManager: cancellableManagerProvider.cancelPreviousAndCreate(), imageFlow: imageFlow)
            }
        }
    }

    func doLoadImageFlow(cancellableManager: CancellableManager, imageFlow: ImageFlow) {
        var unProcessedImage: UIImage?
        if let imageResource = imageFlow.imageResource {
            unProcessedImage = MetaImageResourceManager.shared.image(fromResource: imageResource)
        }
        if let imageResource = imageFlow.placeholderImageResource {
            if let placeholderContentMode = placeholderContentMode {
                self.saveContentMode()
                self.contentMode = placeholderContentMode
            }
            unProcessedImage = MetaImageResourceManager.shared.image(fromResource: imageResource)
        }
        if let unProcessedImage = unProcessedImage {
            if let tintColor = imageFlow.tintColor {
                self.image = unProcessedImage.imageWithTintColor(tintColor.color())
            } else {
                self.image = unProcessedImage
            }
        }

        downloadImageFlowIfNeeded(cancellableManager: cancellableManager, imageFlow: imageFlow)
    }

    func downloadImageFlowIfNeeded(cancellableManager: CancellableManager, imageFlow: ImageFlow) {
        guard let url = imageFlow.url, let URL = URL(string: url) else { return }
        MrFreeze().freeze(objectToFreeze: cancellableManager)

        let dataTask = URLSession.shared.dataTask(with: URL) { [weak self] data, response, error in
            DispatchQueue.main.async {
                guard let self = self else { return }
                if let httpURLResponse = response as? HTTPURLResponse, httpURLResponse.statusCode == 200,
                    let mimeType = response?.mimeType, mimeType.hasPrefix("image"),
                    let data = data, error == nil,
                    let image = UIImage(data: data) {

                    self.restoreContentMode()
                    self.image = image

                    if let onSuccess = imageFlow.onSuccess {
                        self.observeImageFlow(cancellableManager: cancellableManager, imageFlowPublisher: onSuccess)
                    }
                } else if let onError = imageFlow.onError {
                    self.observeImageFlow(cancellableManager: cancellableManager, imageFlowPublisher: onError)
                }
            }
        }

        cancellableManager.add(cancellable: dataTask)
        dataTask.resume()
    }

    private func saveContentMode() {
        savedContentMode = contentMode
    }

    private func restoreContentMode() {
        if let savedContentMode = savedContentMode {
            self.contentMode = savedContentMode
        } else {
            self.savedContentMode = nil
        }
    }

    private var savedContentMode: ContentMode? {
        set(value) {
            objc_setAssociatedObject(self, USER_CONTENT_MODE_KEY, value, objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN)
        }
        get {
            return objc_getAssociatedObject(self, USER_CONTENT_MODE_KEY) as? ContentMode
        }
    }

    @available(*, deprecated, renamed: "contentMode")
    public var imageResourceContentMode: ContentMode {
        set(value) {
            contentMode = value
        }
        get {
            return contentMode
        }
    }

    public var placeholderContentMode: ContentMode? {
        set(value) {
            objc_setAssociatedObject(self, USER_PLACEHOLDER_CONTENT_MODE_KEY, value, objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN)
        }
        get {
            return objc_getAssociatedObject(self, USER_PLACEHOLDER_CONTENT_MODE_KEY) as? ContentMode
        }
    }
}

private let USER_CONTENT_MODE_KEY = UnsafeMutablePointer<Int8>.allocate(capacity: 1)
private let USER_PLACEHOLDER_CONTENT_MODE_KEY = UnsafeMutablePointer<Int8>.allocate(capacity: 1)
private let USER_IMAGERESOURCE_CONTENT_MODE_KEY = UnsafeMutablePointer<Int8>.allocate(capacity: 1)

extension URLSessionTask: Cancellable {}
