package org.macho.beforeandafter.shared.di

import dagger.Component
import org.macho.beforeandafter.gallery.GalleryFragment
import org.macho.beforeandafter.graphe2.GrapheFragment
import org.macho.beforeandafter.preference.DeleteAllDialog
import org.macho.beforeandafter.record.EditActivity
import org.macho.beforeandafter.record.RecordFragment
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(fragment: RecordFragment)
    fun inject(fragment: GalleryFragment)
    fun inject(fragment: GrapheFragment)
    fun inject(fragment: DeleteAllDialog)
    fun inject(activity: EditActivity)
}