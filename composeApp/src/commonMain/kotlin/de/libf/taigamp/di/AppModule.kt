package de.libf.taigamp.di

//import org.koin.compose.viewmodel.dsl.viewModelOf
import de.jensklingenberg.ktorfit.Ktorfit
import de.libf.taigamp.data.api.TaigaApi
import de.libf.taigamp.data.api.TaigaKtorClient
import de.libf.taigamp.data.api.TestApi
import de.libf.taigamp.data.api.createTaigaApi
import de.libf.taigamp.data.repositories.AuthRepository
import de.libf.taigamp.data.repositories.ProjectsRepository
import de.libf.taigamp.data.repositories.SprintsRepository
import de.libf.taigamp.data.repositories.TasksRepository
import de.libf.taigamp.data.repositories.UsersRepository
import de.libf.taigamp.data.repositories.WikiRepository
import de.libf.taigamp.domain.repositories.*
import de.libf.taigamp.state.Session
import de.libf.taigamp.state.Settings
import de.libf.taigamp.ui.screens.commontask.CommonTaskViewModel
import de.libf.taigamp.ui.screens.createtask.CreateTaskViewModel
import de.libf.taigamp.ui.screens.dashboard.DashboardViewModel
import de.libf.taigamp.ui.screens.epics.EpicsViewModel
import de.libf.taigamp.ui.screens.issues.IssuesViewModel
import de.libf.taigamp.ui.screens.kanban.KanbanViewModel
import de.libf.taigamp.ui.screens.login.LoginViewModel
import de.libf.taigamp.ui.screens.main.MainViewModel
import de.libf.taigamp.ui.screens.profile.ProfileViewModel
import de.libf.taigamp.ui.screens.projectselector.ProjectSelectorViewModel
import de.libf.taigamp.ui.screens.scrum.ScrumViewModel
import de.libf.taigamp.ui.screens.settings.SettingsViewModel
import de.libf.taigamp.ui.screens.sprint.SprintViewModel
import de.libf.taigamp.ui.screens.team.TeamViewModel
import de.libf.taigamp.ui.screens.wiki.createpage.WikiCreatePageViewModel
import de.libf.taigamp.ui.screens.wiki.list.WikiListViewModel
import de.libf.taigamp.ui.screens.wiki.page.WikiPageViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.last
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

//TODO
suspend fun getApiUrl(session: Session) = // for compatibility with older app versions
    if (!session.server.value.run { startsWith("https://") || startsWith("http://") }) {
        "https://"
    } else {
        ""
    } + "${session.server.value}/${TaigaApi.API_PREFIX}/"

internal const val dataStoreFileName = "taigamp.preferences_pb"

val dataModule = module {
    single<TaigaApi> {
//        TaigaKtorClient(get())
        Ktorfit.Builder()
//            .baseUrl("http://127.0.0.1:9999/${TaigaApi.API_PREFIX}/")
            .httpClient(get<HttpClient>())
            .build()
            .createTaigaApi()
    }

    single { Session(get()) }
    single { Settings(get()) }
}

val repoModule = module {
    single<IAuthRepository> { AuthRepository(get(), get()) }
    single<IProjectsRepository> { ProjectsRepository(get(), get()) }
    single<ITasksRepository> { TasksRepository(get(), get()) }
    single<IUsersRepository> { UsersRepository(get(), get()) }
    single<ISprintsRepository> { SprintsRepository(get(), get()) }
    single<IWikiRepository> { WikiRepository(get(), get()) }
}

val viewModelModule = module {
//    viewModel { MainViewModel() }
    viewModelOf(::MainViewModel)
//    viewModel { LoginViewModel(get()) }
    viewModelOf(::LoginViewModel)
//    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModelOf(::DashboardViewModel)
//    viewModel { ScrumViewModel(get(), get(), get()) }
 viewModelOf(::ScrumViewModel)
//    viewModel { EpicsViewModel(get(), get()) }
 viewModelOf(::EpicsViewModel)
//    viewModel { ProjectSelectorViewModel(get(), get()) }
    viewModelOf(::ProjectSelectorViewModel)
//    viewModel { SprintViewModel(get(), get(), get()) }
 viewModelOf(::SprintViewModel)
//    viewModel { CommonTaskViewModel(get(), get(), get(), get()) }
    viewModelOf(::CommonTaskViewModel)
//    viewModel { TeamViewModel(get(), get()) }
    viewModelOf(::TeamViewModel)
//    viewModel { SettingsViewModel(get(), get(), get()) }
 viewModelOf(::SettingsViewModel)
//    viewModel { CreateTaskViewModel(get(), get()) }
 viewModelOf(::CreateTaskViewModel)
//    viewModel { IssuesViewModel(get(), get()) }
 viewModelOf(::IssuesViewModel)
//    viewModel { KanbanViewModel(get(), get(), get()) }
 viewModelOf(::KanbanViewModel)
//    viewModel { ProfileViewModel(get(), get(), get()) }
 viewModelOf(::ProfileViewModel)
//    viewModel { WikiListViewModel(get(), get()) }
    viewModelOf(::WikiListViewModel)
//    viewModel { WikiPageViewModel(get(), get()) }
 viewModelOf(::WikiPageViewModel)
//    viewModel { WikiCreatePageViewModel(get()) }
 viewModelOf(::WikiCreatePageViewModel)
}