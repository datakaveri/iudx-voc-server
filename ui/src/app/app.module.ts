import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { ClassesComponent } from './classes/classes.component';
import { PropertyComponent } from './property/property.component';
import { HttpClientModule } from '@angular/common/http';
import { DataService } from './services/data.service';
import { PropertyDetailComponent } from './property-detail/property-detail.component';
import { ClassDetailComponent } from './class-detail/class-detail.component';
import { SearchComponent } from './search/search.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    FooterComponent,
    HeaderComponent,
    ClassesComponent,
    PropertyComponent,
    PropertyDetailComponent,
    ClassDetailComponent,
    SearchComponent
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule],
  providers: [DataService],
  bootstrap: [AppComponent]
})
export class AppModule {}
