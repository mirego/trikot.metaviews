package com.trikot.viewmodels.sample.viewmodels.home

import com.mirego.trikot.streams.reactive.just
import com.mirego.trikot.viewmodels.ListItemViewModel
import com.mirego.trikot.viewmodels.mutable.MutableListViewModel
import com.trikot.viewmodels.sample.navigation.NavigationDelegate
import com.trikot.viewmodels.sample.viewmodels.MutableHeaderListItemViewModel
import com.trikot.viewmodels.sample.viewmodels.MutablePickerListItemViewModel
import org.reactivestreams.Publisher

class PickersViewModel(navigationDelegate: NavigationDelegate) :
    MutableListViewModel<ListItemViewModel>() {
    override var elements: Publisher<List<ListItemViewModel>> = listOf<ListItemViewModel>(
        MutableHeaderListItemViewModel("Spinner"),
        MutablePickerListItemViewModel(),
        MutableHeaderListItemViewModel(".!enabled"),
        MutablePickerListItemViewModel().apply {
            picker.enabled.value = false
        },
        MutableHeaderListItemViewModel("alpha"),
        MutablePickerListItemViewModel().apply {
            picker.alpha = 0.5f.just()
        },
        MutableHeaderListItemViewModel("hidden"),
        MutablePickerListItemViewModel().apply {
            picker.hidden = true.just()
        }
    ).just()
}
