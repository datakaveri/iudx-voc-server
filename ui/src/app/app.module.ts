import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HighlightModule } from 'ngx-highlightjs';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { ClassesComponent } from './classes/classes.component';
import { PropertiesComponent } from './properties/properties.component';
import { HttpClientModule } from '@angular/common/http';
import { SchemaDetailsComponent } from './schema-details/schema-details.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';

import { SearchResultComponent } from './search-result/search-result.component';
import {
  ServiceWorkerModule,
  SwRegistrationOptions
} from '@angular/service-worker';
import { environment } from '../environments/environment';
import { DataModelsComponent } from './data-models/data-models.component';
import { EntitiesComponent } from './entities/entities.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { DataModelDomainComponent } from './data-models/data-model-domain/data-model-domain.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    FooterComponent,
    HeaderComponent,
    ClassesComponent,
    PropertiesComponent,
    SchemaDetailsComponent,
    SearchResultComponent,
    DataModelsComponent,
    EntitiesComponent,
    PageNotFoundComponent,
    DataModelDomainComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HighlightModule,
    AppRoutingModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatCardModule,
    MatTabsModule,
    ServiceWorkerModule.register('ngsw-worker.js')
  ],
  providers: [
    {
      provide: SwRegistrationOptions,
      useFactory: () => ({ enabled: environment.production })
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
