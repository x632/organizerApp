# organizerApp

SplashScreen: Aktivitet som visar loggan - har ingen viewModel

Login (& ViewModel med samma namn): Sköter alla inloggnings och registreringfunktioner. 

MainActivity (& ViewModel med samma namn): Sköter sökningen på youtube. Samt extraheringen av variabler från youtubes-videons HTML.

AddVideoProps (& ViewModel med samma namn): Tar in inputs från användaren. Ev egen titel på videon, val av grupp o.s.v. 

MainView (& ViewModel med samma namn): Sköter huvudvyn - presentationen av videona, caching, all sortering o. s. v. Är appens huvuddel. Viewmodelen har en viktig funktion
som testas på 4-5 punkter. 

ShowVideo (& ViewModel med samma namn)Sköter visningen av vald video

MainViewViewModelTest (com androidTest): testar största och viktigaste funktionen i programmet med 5 olika test. Funktionen som testas finns i MainViewViewModel.

Adapters: Det två recycleViewsadaptrarna som bildar den tvådimensionella huvudvyn.

Utils (singleton): Sköter internetschecken

ViewModelFactory: Sköter dependencyInjection (context)

Models: VideosGlobal, singleton som används för check för att man inte ska kunna lägga till en video som redan finns i ens bibliotek. 
          EntireCategpory: Gruppklassen
          Video: Videoklassen..



Byggd med MVVM-arkitektur fast utan repositorypattern. Databashanteringen och businesslogiken finns båda i de olika Viewmodelsen. UI - funktionerna finns i aktiviteterna (såklart).
