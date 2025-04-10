import de.libf.taigamp.di.dataModule
import de.libf.taigamp.di.platformModule
import de.libf.taigamp.di.repoModule
import de.libf.taigamp.di.viewModelModule
import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(dataModule, repoModule, viewModelModule, platformModule)
    }
}