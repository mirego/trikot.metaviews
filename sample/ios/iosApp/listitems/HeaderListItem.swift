import UIKit
import MetaviewsSample

class HeaderListItem: UIView {
    private let title = UILabel(frame: .zero)

    var item: MetaHeaderListItem? {
        didSet {
            metaView = item
            title.metaLabel = item?.text
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(title)
        title.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            title.topAnchor.constraint(equalTo: topAnchor),
            title.bottomAnchor.constraint(equalTo: bottomAnchor),
            title.leadingAnchor.constraint(equalTo: leadingAnchor),
            title.trailingAnchor.constraint(equalTo: trailingAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
