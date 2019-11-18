import UIKit
import MetaviewsSample

class ListView: UIView {
    private let tableView = UITableView()

    var items: [MetaListItem]? {
        didSet {
            tableView.reloadData()
        }
    }

    var viewModel: ListViewModel? {
        didSet {
            self.items = viewModel?.items
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(tableView)
        tableView.dataSource = self
        tableView.delegate = self
        tableView.translatesAutoresizingMaskIntoConstraints = false

        tableView.register(AutosizingCell<NavigableListItem>.self, forCellReuseIdentifier: AutosizingCell<NavigableListItem>.self.defaultReuseIdentifier)
        tableView.register(AutosizingCell<LabelListItem>.self, forCellReuseIdentifier: AutosizingCell<LabelListItem>.self.defaultReuseIdentifier)
        tableView.register(AutosizingCell<HeaderListItem>.self, forCellReuseIdentifier: AutosizingCell<HeaderListItem>.self.defaultReuseIdentifier)
        tableView.register(AutosizingCell<ViewListItem>.self, forCellReuseIdentifier: AutosizingCell<ViewListItem>.self.defaultReuseIdentifier)
        tableView.register(AutosizingCell<ButtonListItem>.self, forCellReuseIdentifier: AutosizingCell<ButtonListItem>.self.defaultReuseIdentifier)

        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: topAnchor),
            tableView.bottomAnchor.constraint(equalTo: bottomAnchor),
            tableView.leadingAnchor.constraint(equalTo: leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: trailingAnchor)
        ])
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

extension ListView: UITableViewDataSource {

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items?.count ?? 0
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let metaListItem = items?[indexPath.row]

        if let metaListItem = metaListItem as? MetaNavigableListItem {
            let cell = tableView.dequeueReusableCell(withCellType: AutosizingCell<NavigableListItem>.self, for: indexPath)
            cell.view.item = metaListItem
            return cell
        } else if let metaListItem = metaListItem as? MetaLabelListItem {
            let cell = tableView.dequeueReusableCell(withCellType: AutosizingCell<LabelListItem>.self, for: indexPath)
            cell.view.item = metaListItem
            return cell
        } else if let metaListItem = metaListItem as? MetaHeaderListItem {
            let cell = tableView.dequeueReusableCell(withCellType: AutosizingCell<HeaderListItem>.self, for: indexPath)
            cell.view.item = metaListItem
            return cell
        } else if let metaListItem = metaListItem as? MetaViewListItem {
            let cell = tableView.dequeueReusableCell(withCellType: AutosizingCell<ViewListItem>.self, for: indexPath)
            cell.view.item = metaListItem
            return cell
        } else if let metaListItem = metaListItem as? MetaButtonListItem {
            let cell = tableView.dequeueReusableCell(withCellType: AutosizingCell<ButtonListItem>.self, for: indexPath)
            cell.view.item = metaListItem
            return cell
        }

        return UITableViewCell(frame: .zero)
    }
}

extension ListView: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //let metaListItem = items?[indexPath.row]
    }
}
