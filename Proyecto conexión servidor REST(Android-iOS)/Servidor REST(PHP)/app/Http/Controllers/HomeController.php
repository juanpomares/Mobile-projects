<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Requests;
use App\Http\Controllers\Controller;
use Auth;

class HomeController extends Controller
{
    public function getHome()
    {
      if(Auth::check())
        return redirect()->action('CatalogController@getIndex');
      else
        return redirect('auth/login');
    }
}
