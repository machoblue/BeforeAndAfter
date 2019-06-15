package org.macho.beforeandafter.shared

interface BaseContract {
    interface View<T> {
//        fun setPresenter(presenter: T)
        var presenter: T
    }
    interface Presenter {
        fun start()
    }
}