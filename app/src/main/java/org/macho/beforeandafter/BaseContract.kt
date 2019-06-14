package org.macho.beforeandafter

interface BaseContract {
    interface View<T> {
//        fun setPresenter(presenter: T)
        var presenter: T
    }
    interface Presenter {
        fun start()
    }
}