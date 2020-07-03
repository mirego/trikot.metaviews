package com.trikot.viewmodels.sample.viewmodels.home

import com.mirego.trikot.streams.reactive.just
import com.mirego.trikot.viewmodels.ListItemViewModel
import com.mirego.trikot.viewmodels.mutable.MutableListViewModel
import com.trikot.viewmodels.sample.viewmodels.MutableSliderListItemViewModel
import com.trikot.viewmodels.sample.navigation.NavigationDelegate
import com.trikot.viewmodels.sample.viewmodels.MutableHeaderListItemViewModel
import org.reactivestreams.Publisher

class SlidersViewModel(navigationDelegate: NavigationDelegate) : MutableListViewModel<ListItemViewModel>()  {
    override var elements: Publisher<List<ListItemViewModel>> = listOf<ListItemViewModel>(
        MutableHeaderListItemViewModel("Slider"),
        MutableSliderListItemViewModel(),

        MutableHeaderListItemViewModel(".hidden"),
        MutableSliderListItemViewModel().apply {
            slider.hidden = true.just()
            valueLabel.hidden = true.just()
        },

        MutableHeaderListItemViewModel(".alpha"),
        MutableSliderListItemViewModel().apply {
            slider.alpha = 0.5f.just()
        }
    ).just()
}
