@extends('layouts.master')

@section('content')

<div class="row">
    <div class="col-sm-4">

		<img src="{{$pelicula['poster']}}"/>

    </div>
    <div class="col-sm-8">
		<h2>{{$pelicula->title}}</h2>
		<h3>Año: {{$pelicula->year}}</h3>
		<h3>Director: {{$pelicula->director}}</h3>
		<br>
		<p><strong>Resumen: </strong>{{$pelicula->synopsis}}</p>
		<br>
		<p><strong>Estado: </strong>{{$pelicula->rented?'Película actualmente alquilada':'Película disponible'}}</p>

		<p>

		<form action="{{$pelicula->rented?action('CatalogController@putReturn', $pelicula->id):action('CatalogController@putRent', $pelicula->id)}}" method="POST" style="display:inline">
			{{ method_field('PUT') }}
			{!! csrf_field() !!}
			<button type="submit" class="btn {{$pelicula->rented?'btn-danger':'btn-success'}}" style="display:inline">
				{{$pelicula->rented?'Devolver película':'Alquilar película'}}
			</button>
		</form>

		<a class="btn btn-warning" href={{action('CatalogController@getEdit',$pelicula->id)}}>Editar película</a>

		<form action="{{action('CatalogController@deleteMovie', $pelicula->id)}}"
			method="POST" style="display:inline">
			{{ method_field('DELETE') }}
			{!! csrf_field() !!}
			<button type="submit" class="btn btn-danger" style="display:inline">
				Eliminar película
			</button>
		</form>


		<a class="btn btn-default" href={{action('CatalogController@getIndex')}} >Volver al listado</a>



		</p>
    </div>
</div>
@stop
