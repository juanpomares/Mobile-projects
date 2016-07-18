<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Http\Requests;
use App\Http\Controllers\Controller;
use App\Movie;


class APICatalogController extends Controller
{
    public function index()
    {
		return response()->json( Movie::all() );
    }
	
	public function show($id)
    {
		$pelicula=Movie::findOrFail($id);
		return response()->json($pelicula);
    }
	
	public function putRent($id)
	{
		$m = Movie::findOrFail( $id );
		$m->rented = true;
		$m->save();

		return response()->json( ['error' => false,
							   'msg' => 'La película se ha marcado como alquilada' ] );
	}
	
	public function putReturn($id)
	{
		$m = Movie::findOrFail( $id );
		$m->rented = false;
		$m->save();

		return response()->json( ['error' => false,
							   'msg' => 'La película se ha marcado como disponible' ] );
	}
	
    public function store(Request $request)
    {
        $p = new Movie;
		$p->title = $request->input('title');
		$p->year = $request->input('year');
		$p->director = $request->input('director');
		$p->poster = $request->input('poster');
		$p->rented = false;
		$p->synopsis = $request->input('synopsis');
		$p->save();
		
		return response()->json( ['error' => false,
							   'msg' => 'La película se ha creado correctamente' ] );
    }

    public function update(Request $request, $id)
    {
		$movie = Movie::findOrFail($id);
		$movie->title = $request->input('title');
		$movie->year = $request->input('year');
		$movie->director = $request->input('director');
		$movie->poster = $request->input('poster');
		$movie->synopsis = $request->input('synopsis');
		$movie->save();
		
		return response()->json( ['error' => false,
							   'msg' => 'La película se ha editado correctamente' ] );
    }

    public function destroy($id)
    {
        $m = Movie::findOrFail( $id );
		$m->destroy();

		return response()->json( ['error' => false,
							   'msg' => 'La película se ha eliminado correctamente' ] );
	
    }
}
