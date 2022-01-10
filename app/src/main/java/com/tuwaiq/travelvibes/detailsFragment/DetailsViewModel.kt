package com.tuwaiq.travelvibes.detailsFragment

import androidx.lifecycle.ViewModel
import com.tuwaiq.travelvibes.AppRepository

class DetailsViewModel:ViewModel() {

    private val repo: AppRepository = AppRepository.getInstance()
}