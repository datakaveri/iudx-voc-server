import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ClassesComponent } from './classes/classes.component';
import { HomeComponent } from './home/home.component';
import { PropertyComponent } from './property/property.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'classes',
    component: ClassesComponent
  },
  {
    path: 'property',
    component: PropertyComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
