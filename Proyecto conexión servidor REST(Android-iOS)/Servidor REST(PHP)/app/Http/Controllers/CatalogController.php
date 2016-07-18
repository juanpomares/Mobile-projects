<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Http\Requests;
use App\Http\Controllers\Controller;
use App\Movie;
use Notification;
use Auth;

class CatalogController extends Controller
{
    //
    public function getIndex()
    {
		  $movies=Movie::all();
    	return view('catalog.index', array('arrayPeliculas'=>$movies));
    }

    public function getShow($id)
    {
		  $movie = Movie::findOrFail($id);
    	return view('catalog.show', array('pelicula'=>$movie));
    }

    public function getCreate()
    {
    	return view('catalog.create');
    }

	public function getEdit($id)
	{
		  $movie = Movie::findOrFail($id);
   		return view('catalog.edit', array('pelicula'=>$movie));
	}

	public function postCreate(Request $request)
    {
    	$p = new Movie;
  		$p->title = $request->input('title');
  		$p->year = $request->input('year');
  		$p->director = $request->input('director');
  		$p->poster = $request->input('poster');
  		$p->rented = false;
  		$p->synopsis = $request->input('synopsis');
  		$p->save();


  		Notification::success('La película se ha guardado/modificado correctamente');

  	  return redirect('catalog');
    }

    public function putedit($id, Request $request)
    {
		$movie = Movie::findOrFail($id);
		$movie->title = $request->input('title');
		$movie->year = $request->input('year');
		$movie->director = $request->input('director');
		$movie->poster = $request->input('poster');
		$movie->synopsis = $request->input('synopsis');
		$movie->save();


		Notification::success('La película se ha guardado/modificado correctamente');

    	return redirect()->action('CatalogController@getShow', $id);
	}

	public function putRent($id)
	{
		$movie = Movie::findOrFail($id);
		$movie->rented=true;
		$movie->save();

		Notification::success('La película se ha alquilado correctamente');
		return redirect()->action('CatalogController@getShow', $id);
	}

	public function putReturn($id)
	{
		$movie = Movie::findOrFail($id);
		$movie->rented=false;
		$movie->save();

		Notification::success('La película se ha devuelto correctamente');
		return redirect()->action('CatalogController@getShow', $id);
	}

	public function deleteMovie($id)
	{
		$movie = Movie::findOrFail($id);
		$movie->delete();

		Notification::success('La película se ha eliminado correctamente');
		return redirect('catalog');
	}

}
