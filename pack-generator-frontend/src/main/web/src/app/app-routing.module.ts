import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LogFileComponent } from './log-file/log-file.component';
import { PackgeneratorComponent } from './packgenerator/packgenerator.component';



const routes: Routes = [
    {
      path: 'pack-generator',
      component: PackgeneratorComponent
    },
    {
      path: 'logfile',
      component: LogFileComponent
    }
]


@NgModule({
    imports: [RouterModule.forRoot(routes, { useHash: true })],
    exports: [RouterModule]
  })
  export class AppRoutingModule {
    static components = [
        PackgeneratorComponent
    ];
  }