package com.trikot.viewmodels.sample.viewmodels

import com.mirego.trikot.viewmodels.LabelViewModel
import com.mirego.trikot.viewmodels.ListItemViewModel

interface LabelListItemViewModel : ListItemViewModel {
    val label: LabelViewModel
}
