package org.macho.beforeandafter.record

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class RecordModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun recordFragment(): RecordFragment

    // メモ：↓Fragmentに注すPresenterが@ActivityScopedになっていることについて。
    // Googleのサンプルに習って、こうしている。GoogleのサンプルはActivity: Fragment: Presenter = 1:1:1である(Activityが汚れ役?)ため、こうしているっぽい。
    //
    // このアプリでは、理想的にはFragmentに対応するSubComponentに、(Sub)Modulesを設定するのがよい。
    // 　(新しく別のModuleを作って、↑の@ContributesAndroidInjector.modulesに設定する。)
    // 他のFragmentから、このPresenterがInjectされてしまうことも防げるし。
    //
    // ただ、細かく分けすぎである(コードが増える)のと、実質Presenterの生存期間はかわらないはず(未検証)なので、
    // Fragmentの下にPresenterを持ってくるのではなく、このままFragmentとPresenterは対等に扱うことにする。
    @ActivityScoped
    @Binds
    abstract fun recordPresenter(recordPresenter: RecordPresenter): RecordContract.Presenter
}