package com.tuwaiq.travelvibes.authentication

import androidx.fragment.app.Fragment

interface FragmentNavigation {

    fun navigateFrag(fragment: Fragment , addToStack:Boolean)
}