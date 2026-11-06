import { Component, inject, signal } from '@angular/core';
import { CategoriaService } from '../../../core/services/categoria.service';
import { FiltroCategoriasService } from '../../../core/services/filtro-categorias.service';
import { Categoria } from '../../../models/categoria';

@Component({
  selector: 'app-categorias',
  templateUrl: './categorias.html',
  styleUrl: './categorias.scss'
})
export class Categorias {
  private readonly categoriaService = inject(CategoriaService);

  protected readonly filtro = inject(FiltroCategoriasService);
  protected readonly categorias = signal<Categoria[]>([]);

  constructor() {
    this.categoriaService.listar().subscribe({
      next: (data) => this.categorias.set(data),
      error: () => this.categorias.set([])
    });
  }
}
