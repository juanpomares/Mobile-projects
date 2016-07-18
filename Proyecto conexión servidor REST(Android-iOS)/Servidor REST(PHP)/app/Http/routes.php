<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the controller to call when that URI is requested.
|
*/

Route::get('/',  'HomeController@getHome');

/*
Route::get('/auth/login', function () {
    return view("auth.login");
});

Route::get('/auth/logout', function () {
    return "Logout usuario";
});*/

Route::get('auth/login', 'Auth\AuthController@getLogin');
Route::post('auth/login', 'Auth\AuthController@postLogin');

/*
Route::get('auth/register', 'Auth\AuthController@getRegister');
Route::post('auth/register','Auth\AuthController@postRegister');
*/


Route::group(['middleware' => 'auth'], function()
{
	Route::get('catalog', 'CatalogController@getIndex');
	Route::get('catalog/show/{id}', 'CatalogController@getShow');
	Route::get('catalog/create', 'CatalogController@getCreate');
	Route::post('catalog/create', 'CatalogController@postCreate');

	Route::get('catalog/edit/{id}', 'CatalogController@getEdit');
	Route::put('catalog/edit/{id}', 'CatalogController@putEdit');


	Route::put('catalog/rent/{id}', 'CatalogController@putRent');
	Route::put('catalog/return/{id}', 'CatalogController@putReturn');
	Route::delete('catalog/delete/{id}', 'CatalogController@deleteMovie');


	Route::get('auth/logout', 'Auth\AuthController@getLogout');
});


Route::group(['prefix' => 'api'], function()
{
    Route::group(['prefix' => 'v1'], function()
    {

		Route::resource('catalog', 'APICatalogController',   ['only' => ['index', 'show']]);
		Route::group(['middleware' => 'auth.basic.once'], function()
		{
			Route::resource('catalog', 'APICatalogController',   ['except' => ['index', 'show']]);
			Route::put('catalog/{id}/rent', 'APICatalogController@putRent');
			Route::put('catalog/{id}/return', 'APICatalogController@putReturn');
		});

		/*
        // Rutas con el prefijo api/v1
        Route::get('catalog', 'APICatalogController@index');
		Route::get('catalog/{id}', 'APICatalogController@show');

		Route::group(['middleware' => 'auth.basic.once'], function()
		{
			Route::post('catalog', 'APICatalogController@store');

			Route::put('catalog/{id}', 'APICatalogController@update');
			Route::delete('catalog/{id}', 'APICatalogController@destroy');

			Route::put('catalog/{id}/rent', 'APICatalogController@putRent');
			Route::put('catalog/{id}/return', 'APICatalogController@putReturn');
		});*/

    });

});
