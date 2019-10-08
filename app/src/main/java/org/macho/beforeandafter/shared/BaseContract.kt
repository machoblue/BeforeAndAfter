package org.macho.beforeandafter.shared

interface BaseContract {
    interface View<T> {
//        fun setPresenter(presenter: T)
        var presenter: T
    }
    interface Presenter<T> {
//        fun start()
        fun takeView(view: T)
        fun dropView()
    }
}