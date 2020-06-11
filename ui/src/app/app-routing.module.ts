import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ClassesComponent } from './classes/classes.component';
import { HomeComponent } from './home/home.component';
import { PropertiesComponent } from './properties/properties.component';
import { SchemaDetailsComponent } from './schema-details/schema-details.component';
import { DataModelsComponent } from './data-models/data-models.component';
import { EntitiesComponent } from './entities/entities.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'list/classes',
    component: ClassesComponent
  },
  {
    path: 'list/properties',
    component: PropertiesComponent
  },
  {
    path: 'datamodels',
    component: DataModelsComponent
  },
  {
    path: 'entities',
    component: EntitiesComponent
  },
  {
    path: ':schemaName',
    component: SchemaDetailsComponent
  },
  {
    path: 'search/:schemaName',
    component: SchemaDetailsComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
