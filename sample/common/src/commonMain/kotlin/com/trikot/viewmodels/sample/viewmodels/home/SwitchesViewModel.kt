package com.trikot.viewmodels.sample.viewmodels.home

import com.mirego.trikot.streams.reactive.just
import com.mirego.trikot.viewmodels.ListItemViewModel
import com.mirego.trikot.viewmodels.mutable.MutableListViewModel
import com.mirego.trikot.viewmodels.properties.ViewModelAction
import com.trikot.viewmodels.sample.navigation.NavigationDelegate
import com.trikot.viewmodels.sample.viewmodels.MutableHeaderListItemViewModel
import com.trikot.viewmodels.sample.viewmodels.MutableToggleSwitchListItemViewModel
import org.reactivestreams.Publisher

class SwitchesViewModel(navigationDelegate: NavigationDelegate) :
    MutableListViewModel<ListItemViewModel>() {
    override var elements: Publisher<List<ListItemViewModel>> = listOf<ListItemViewModel>(
        MutableHeaderListItemViewModel("Switch"),
        MutableToggleSwitchListItemViewModel().apply {
            toggleSwitch.action = ViewModelAction {
                // Insert Action Here
            }.just()
        },
        MutableHeaderListItemViewModel(".hidden"),
        MutableToggleSwitchListItemViewModel().apply {
            toggleSwitch.hidden = true.just()
        },
        MutableHeaderListItemViewModel(".!enabled"),
        MutableToggleSwitchListItemViewModel().apply {
            toggleSwitch.isEnabled = false.just()
        },
        MutableHeaderListItemViewModel(".alpha"),
        MutableToggleSwitchListItemViewModel().apply {
            toggleSwitch.alpha = 0.5f.just()
            toggleSwitch.action = ViewModelAction {
                // Insert Action Here
            }.just()
        }
    ).just()
}
