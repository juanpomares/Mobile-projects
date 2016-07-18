@extends('layouts.master')

@section('content')

<div class="row" style="margin-top:20px">

  <div class="col-md-offset-3 col-md-6">

    <div class="panel panel-default">
      <div class="panel-heading">
        <h3 class="panel-title text-center">
          <span class="glyphicon glyphicon-film" aria-hidden="true"></span>
          Modificar película
        </h3>
      </div>

      <div class="panel-body" style="padding:30px">

        <form method="POST">
          {{ csrf_field() }}
          {{ method_field('PUT') }}

            <div class="form-group">
              <label for="title">Título</label>
              <input type="text" name="title" id="title" class="form-control" value="{{$pelicula->title}}">
          </div>

          <div class="form-group">
            <label for="year">Año</label>
            <input type="number" id="year" name="year" class="form-control" value="{{$pelicula->year}}">
          </div>

          <div class="form-group">
            <label for="director">Director</label>
            <input type="text" id="director" name="director" class="form-control" value="{{$pelicula->director}}">
          </div>

          <div class="form-group">
            <label for="director">Poster</label>
            <input type="text" id="poster" name="poster" class="form-control" value="{{$pelicula->poster}}">
          </div>

          <div class="form-group">
            <label for="synopsis">Resumen</label>
              <textarea name="synopsis" id="synopsis" class="form-control" rows="3"> {{$pelicula->synopsis}}"</textarea>
          </div>

          <div class="form-group text-center">
            <button type="submit" class="btn btn-primary" style="padding:8px 100px;margin-top:25px;">
              Modificar película
            </button>
          </div>

        </form>

      </div>
    </div>
  </div>
</div>

@stop
