package de.libf.taigamp.dagger

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import de.libf.taigamp.ui.screens.login.LoginViewModel
import de.libf.taigamp.ui.screens.main.MainViewModel
import de.libf.taigamp.ui.screens.projectselector.ProjectSelectorViewModel
import de.libf.taigamp.ui.screens.scrum.ScrumViewModel
import de.libf.taigamp.ui.screens.sprint.SprintViewModel
import de.libf.taigamp.ui.screens.commontask.CommonTaskViewModel
import de.libf.taigamp.ui.screens.createtask.CreateTaskViewModel
import de.libf.taigamp.ui.screens.dashboard.DashboardViewModel
import de.libf.taigamp.ui.screens.epics.EpicsViewModel
import de.libf.taigamp.ui.screens.issues.IssuesViewModel
import de.libf.taigamp.ui.screens.kanban.KanbanViewModel
import de.libf.taigamp.ui.screens.profile.ProfileViewModel
import de.libf.taigamp.ui.screens.settings.SettingsViewModel
import de.libf.taigamp.ui.screens.team.TeamViewModel
import de.libf.taigamp.ui.screens.wiki.createpage.WikiCreatePageViewModel
import de.libf.taigamp.ui.screens.wiki.page.WikiPageViewModel
import de.libf.taigamp.ui.screens.wiki.list.WikiListViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, RepositoriesModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance fun context(context: Context): Builder
        fun build(): AppComponent
    }

    fun inject(mainViewModel: MainViewModel)
    fun inject(loginViewModel: LoginViewModel)
    fun inject(dashboardViewModel: DashboardViewModel)
    fun inject(scrumViewModel: ScrumViewModel)
    fun inject(epicsViewModel: EpicsViewModel)
    fun inject(projectSelectorViewModel: ProjectSelectorViewModel)
    fun inject(sprintViewModel: SprintViewModel)
    fun inject(commonTaskViewModel: CommonTaskViewModel)
    fun inject(teamViewModel: TeamViewModel)
    fun inject(settingsViewModel: SettingsViewModel)
    fun inject(createTaskViewModel: CreateTaskViewModel)
    fun inject(issuesViewModel: IssuesViewModel)
    fun inject(kanbanViewModel: KanbanViewModel)
    fun inject(profileViewModel: ProfileViewModel)
    fun inject(wikiSelectorViewModel: WikiListViewModel)
    fun inject(wikiPageViewModel: WikiPageViewModel)
    fun inject(wikiCreatePageViewModel: WikiCreatePageViewModel)
}