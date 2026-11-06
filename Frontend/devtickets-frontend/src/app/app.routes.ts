import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Registro } from './features/auth/registro/registro';
import { EventList } from './features/tickets/event-list/event-list';
import { MisEntradas } from './features/tickets/mis-entradas/mis-entradas';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';

const soloAutenticados = () =>
  inject(AuthService).autenticado() ? true : inject(Router).parseUrl('/login');

export const routes: Routes = [
  { path: '', component: EventList },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: 'mis-entradas', component: MisEntradas, canActivate: [soloAutenticados] },
  { path: '**', redirectTo: '' }
];
