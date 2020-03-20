package com.trikot.viewModels.sample.viewModels.home

import com.mirego.trikot.viewModels.lifecycle.ApplicationState
import com.mirego.trikot.viewModels.lifecycle.ApplicationStatePublisher
import com.mirego.trikot.viewModels.properties.ViewModelAction
import com.mirego.trikot.streams.reactive.PublishSubject
import com.mirego.trikot.streams.reactive.Publishers
import com.mirego.trikot.streams.reactive.distinctUntilChanged
import com.mirego.trikot.streams.reactive.filter
import com.mirego.trikot.streams.reactive.just
import com.mirego.trikot.streams.reactive.map
import com.mirego.trikot.streams.reactive.shared
import com.trikot.viewModels.sample.viewModels.ListItemViewModel
import com.trikot.viewModels.sample.viewModels.MutableHeaderListItemViewModel
import com.trikot.viewModels.sample.viewModels.MutableLabelListItemViewModel
import com.trikot.viewModels.sample.viewModels.MutableNavigableListItemViewModel
import com.trikot.viewModels.sample.navigation.Destination
import com.trikot.viewModels.sample.navigation.NavigationDelegate
import org.reactivestreams.Publisher

class HomeViewModelImpl(private val delegate: NavigationDelegate) :
    ListViewModel {

    private val applicationStatePublisher = ApplicationStatePublisher()

    private val backgroundCount = Publishers.publishSubject<Int>()
    private val backgroundStateCounter =
        applicationStatePublisher.distinctUntilChanged()
            .filter { it == ApplicationState.FOREGROUND }
            .incrementCounter(backgroundCount)
            .shared()

    override val items = listOf(
        MutableNavigableListItemViewModel().also {
            it.title.text = "Views ->".just()
            it.action = ViewModelAction { delegate.navigateTo(Destination.VIEWS) }.just()
            it.title.action = it.action
        } as ListItemViewModel,

        MutableNavigableListItemViewModel().also {
            it.title.text = "Labels ->".just()
            it.action = ViewModelAction { delegate.navigateTo(Destination.LABELS) }.just()
            it.title.action = it.action
        } as ListItemViewModel,

        MutableNavigableListItemViewModel().also {
            it.title.text = "Buttons ->".just()
            it.action = ViewModelAction { delegate.navigateTo(Destination.BUTTONS) }.just()
            it.title.action = it.action
        } as ListItemViewModel,

        MutableNavigableListItemViewModel().also {
            it.title.text = "Images ->".just()
            it.action = ViewModelAction { delegate.navigateTo(Destination.IMAGES) }.just()
            it.title.action = it.action
        } as ListItemViewModel,

        MutableNavigableListItemViewModel().also {
            it.title.text = "Input text ->".just()
            it.action = ViewModelAction { delegate.navigateTo(Destination.INPUT_TEXT) }.just()
            it.title.action = it.action
        } as ListItemViewModel,

        MutableHeaderListItemViewModel("Application state"),

        MutableLabelListItemViewModel().apply {
            label.text = backgroundStateCounter.map { "Went to foreground $it times" }
        }
    )
}

fun <T> Publisher<T>.incrementCounter(
    counter: PublishSubject<Int>
): Publisher<Int> {
    return map {
        val currentCount = counter.value ?: 0
        val nextCount = currentCount + 1
        counter.value = nextCount
        nextCount
    }
}