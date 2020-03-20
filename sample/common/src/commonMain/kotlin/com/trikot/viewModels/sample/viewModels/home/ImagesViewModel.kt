package com.trikot.viewModels.sample.viewModels.home

import com.mirego.trikot.viewModels.ImageFlow
import com.mirego.trikot.viewModels.mutable.simpleImageFlowProvider
import com.mirego.trikot.viewModels.properties.Color
import com.mirego.trikot.viewModels.properties.ImageState
import com.mirego.trikot.viewModels.properties.ViewModelAction
import com.mirego.trikot.viewModels.properties.StateSelector
import com.mirego.trikot.viewModels.properties.SimpleImageFlow
import com.mirego.trikot.streams.reactive.Publishers
import com.mirego.trikot.streams.reactive.just
import com.mirego.trikot.streams.reactive.map
import com.trikot.viewModels.sample.viewModels.ListItemViewModel
import com.trikot.viewModels.sample.viewModels.MutableHeaderListItemViewModel
import com.trikot.viewModels.sample.viewModels.MutableImageListItemViewModel
import com.trikot.viewModels.sample.viewModels.MutableViewListItemViewModel
import com.trikot.viewModels.sample.navigation.NavigationDelegate
import com.trikot.viewModels.sample.resource.ImageResources

class ImagesViewModel(navigationDelegate: NavigationDelegate) : ListViewModel {
    private val fallbackImageFlow =
        Publishers.behaviorSubject(SimpleImageFlow(url = "https://www.vokode.com/wp-content/uploads/2019/06/fallback.jpg") as ImageFlow)

    override val items: List<ListItemViewModel> = listOf(
        MutableHeaderListItemViewModel(".backgroundColor"),
        MutableViewListItemViewModel().also {
            it.view.backgroundColor = StateSelector(Color(143, 143, 143)).just()
        },
        MutableHeaderListItemViewModel(".alpha"),
        MutableImageListItemViewModel(simpleImageFlowProvider()).also {
            it.image.alpha = 0.5f.just()
        },
        MutableHeaderListItemViewModel(".hidden"),
        MutableImageListItemViewModel(simpleImageFlowProvider()).also {
            it.image.hidden = true.just()
        },
        MutableHeaderListItemViewModel(".onTap"),
        MutableImageListItemViewModel(simpleImageFlowProvider()).also {
            it.image.action = ViewModelAction { navigationDelegate.showAlert("Tapped $it") }.just()
        },
        MutableHeaderListItemViewModel(".imageResource"),
        MutableImageListItemViewModel(simpleImageFlowProvider(imageResource = ImageResources.ICON)),
        MutableHeaderListItemViewModel(".imageResource + tintColor"),
        MutableImageListItemViewModel(
            simpleImageFlowProvider(
                imageResource = ImageResources.ICON,
                tintColor = Color(255, 0, 0)
            )
        ),
        MutableHeaderListItemViewModel(".placeholder"),
        MutableImageListItemViewModel(simpleImageFlowProvider(placeholderImageResource = ImageResources.ICON)),
        MutableHeaderListItemViewModel(".placeholder + .url"),
        MutableImageListItemViewModel(
            simpleImageFlowProvider(
                url = "https://images5.alphacoders.com/346/thumb-1920-346532.jpg",
                placeholderImageResource = ImageResources.ICON
            )
        ),
        MutableHeaderListItemViewModel("url + .onSuccess"),
        MutableImageListItemViewModel({ _, _ ->
            Publishers.behaviorSubject(
                SimpleImageFlow(
                    url = "https://images5.alphacoders.com/346/thumb-1920-346532.jpg",
                    onSuccess = fallbackImageFlow
                )
            )
        }),
        MutableHeaderListItemViewModel("url + .onError"),
        MutableImageListItemViewModel({ _, _ ->
            Publishers.behaviorSubject(
                SimpleImageFlow(
                    url = "https://not.existing.url.foo",
                    onError = fallbackImageFlow
                )
            )
        }),
        MutableHeaderListItemViewModel("url + errorState"),
        MutableImageListItemViewModel({ _, _ -> Publishers.behaviorSubject(SimpleImageFlow(url = "https://not.existing.url.foo")) }).apply {
            image.backgroundColor = image.imageState.map {
                StateSelector(
                    if (it == ImageState.ERROR) Color(
                        255,
                        0,
                        0
                    ) else Color(0, 255, 0)
                )
            }
        }
    )
}
