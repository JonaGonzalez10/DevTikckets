import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './shared/components/navbar/navbar';
import { Categorias } from './shared/components/categorias/categorias';
import { Footer } from './shared/components/footer/footer';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Categorias, Footer],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {}
