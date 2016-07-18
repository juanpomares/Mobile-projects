<?php

use Illuminate\Database\Seeder;
use Illuminate\Database\Eloquent\Model;
use App\Movie;
use App\User;

class DatabaseSeeder extends Seeder
{
	private $arrayPeliculas = array(
		array(
			'title' => 'El padrino',
			'year' => '1972', 
			'director' => 'Francis Ford Coppola', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMjEyMjcyNDI4MF5BMl5BanBnXkFtZTcwMDA5Mzg3OA@@._V1_SX214_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Don Vito Corleone (Marlon Brando) es el respetado y temido jefe de una de las cinco familias de la mafia de Nueva York. Tiene cuatro hijos: Connie (Talia Shire), el impulsivo Sonny (James Caan), el pusilánime Freddie (John Cazale) y Michael (Al Pacino), que no quiere saber nada de los negocios de su padre. Cuando Corleone, en contra de los consejos de \'Il consigliere\' Tom Hagen (Robert Duvall), se niega a intervenir en el negocio de las drogas, el jefe de otra banda ordena su asesinato. Empieza entonces una violenta y cruenta guerra entre las familias mafiosas.'
		),
		array(
			'title' => 'El Padrino. Parte II',
			'year' => '1974', 
			'director' => 'Francis Ford Coppola', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BNDc2NTM3MzU1Nl5BMl5BanBnXkFtZTcwMTA5Mzg3OA@@._V1_SX214_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Continuación de la historia de los Corleone por medio de dos historias paralelas: la elección de Michael Corleone como jefe de los negocios familiares y los orígenes del patriarca, el ya fallecido Don Vito, primero en Sicilia y luego en Estados Unidos, donde, empezando desde abajo, llegó a ser un poderosísimo jefe de la mafia de Nueva York.'
		),
		array(
			'title' => 'La lista de Schindler',
			'year' => '1993', 
			'director' => 'Steven Spielberg', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMzMwMTM4MDU2N15BMl5BanBnXkFtZTgwMzQ0MjMxMDE@._V1_SX214_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Segunda Guerra Mundial (1939-1945). Oskar Schindler (Liam Neeson), un hombre de enorme astucia y talento para las relaciones públicas, organiza un ambicioso plan para ganarse la simpatía de los nazis. Después de la invasión de Polonia por los alemanes (1939), consigue, gracias a sus relaciones con los nazis, la propiedad de una fábrica de Cracovia. Allí emplea a cientos de operarios judíos, cuya explotación le hace prosperar rápidamente. Su gerente (Ben Kingsley), también judío, es el verdadero director en la sombra, pues Schindler carece completamente de conocimientos para dirigir una empresa.'
		),
		array(
			'title' => 'Pulp Fiction',
			'year' => '1994', 
			'director' => 'Quentin Tarantino', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMjE0ODk2NjczOV5BMl5BanBnXkFtZTYwNDQ0NDg4._V1_SY317_CR4,0,214,317_AL_.jpg', 
			'rented' => true, 
			'synopsis' => 'Jules y Vincent, dos asesinos a sueldo con muy pocas luces, trabajan para Marsellus Wallace. Vincent le confiesa a Jules que Marsellus le ha pedido que cuide de Mia, su mujer. Jules le recomienda prudencia porque es muy peligroso sobrepasarse con la novia del jefe. Cuando llega la hora de trabajar, ambos deben ponerse manos a la obra. Su misión: recuperar un misterioso maletín. '
		),
		array(
			'title' => 'Cadena perpetua',
			'year' => '1994', 
			'director' => 'Frank Darabont', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BODU4MjU4NjIwNl5BMl5BanBnXkFtZTgwMDU2MjEyMDE@._V1_SX214_AL_.jpg', 
			'rented' => true, 
			'synopsis' => 'Acusado del asesinato de su mujer, Andrew Dufresne (Tim Robbins), tras ser condenado a cadena perpetua, es enviado a la cárcel de Shawshank. Con el paso de los años conseguirá ganarse la confianza del director del centro y el respeto de sus compañeros de prisión, especialmente de Red (Morgan Freeman), el jefe de la mafia de los sobornos.'
		),
		array(
			'title' => 'El golpe',
			'year' => '1973', 
			'director' => 'George Roy Hill', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTY5MjM1OTAyOV5BMl5BanBnXkFtZTgwMDkwODg4MDE@._V1._CR52,57,915,1388_SX214_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Chicago, años treinta. Redford y Newman son dos timadores que deciden vengar la muerte de un viejo y querido colega, asesinado por orden de un poderoso gángster (Robert Shaw). Para ello urdirán un ingenioso y complicado plan con la ayuda de todos sus amigos y conocidos.'
		),
		array(
			'title' => 'La vida es bella',
			'year' => '1997', 
			'director' => 'Roberto Benigni', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTQwMTM2MjE4Ml5BMl5BanBnXkFtZTgwODQ2NTYxMTE@._V1_SX214_AL_.jpg', 
			'rented' => true, 
			'synopsis' => 'En 1939, a punto de estallar la Segunda Guerra Mundial (1939-1945), el extravagante Guido llega a Arezzo (Toscana) con la intención de abrir una librería. Allí conoce a Dora y, a pesar de que es la prometida del fascista Ferruccio, se casa con ella y tiene un hijo. Al estallar la guerra, los tres son internados en un campo de exterminio, donde Guido hará lo imposible para hacer creer a su hijo que la terrible situación que están padeciendo es tan sólo un juego.'
		),
		array(
			'title' => 'Uno de los nuestros',
			'year' => '1990', 
			'director' => 'Martin Scorsese', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTY2OTE5MzQ3MV5BMl5BanBnXkFtZTgwMTY2NTYxMTE@._V1_SX214_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Henry Hill, hijo de padre irlandés y madre siciliana, vive en Brooklyn y se siente fascinado por la vida que llevan los gángsters de su barrio, donde la mayoría de los vecinos son inmigrantes. Paul Cicero, el patriarca de la familia Pauline, es el protector del barrio. A los trece años, Henry decide abandonar la escuela y entrar a formar parte de la organización mafiosa como chico de los recados; muy pronto se gana la confianza de sus jefes, gracias a lo cual irá subiendo de categoría. '
		),
		array(
			'title' => 'Alguien voló sobre el nido del cuco',
			'year' => '1975', 
			'director' => 'Milos Forman', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTk5OTA4NTc0NF5BMl5BanBnXkFtZTcwNzI5Mzg3OA@@._V1_SY317_CR12,0,214,317_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Randle McMurphy (Jack Nicholson), un hombre condenado por asalto, y un espíritu libre que vive contracorriente, es recluido en un hospital psiquiátrico. La inflexible disciplina del centro acentúa su contagiosa tendencia al desorden, que acabará desencadenando una guerra entre los pacientes y el personal de la clínica con la fría y severa enfermera Ratched (Louise Fletcher) a la cabeza. La suerte de cada paciente del pabellón está en juego.'
		),
		array(
			'title' => 'American History X',
			'year' => '1998', 
			'director' => 'Tony Kaye', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMjMzNDUwNTIyMF5BMl5BanBnXkFtZTcwNjMwNDg3OA@@._V1_SY317_CR17,0,214,317_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Derek (Edward Norton), un joven "skin head" californiano de ideología neonazi, fue encarcelado por asesinar a un negro que pretendía robarle su furgoneta. Cuando sale de prisión y regresa a su barrio dispuesto a alejarse del mundo de la violencia, se encuentra con que su hermano pequeño (Edward Furlong), para quien Derek es el modelo a seguir, sigue el mismo camino que a él lo condujo a la cárcel.'
		),
		array(
			'title' => 'Sin perdón',
			'year' => '1992', 
			'director' => 'Clint Eastwood', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTkzNTc0NDc4OF5BMl5BanBnXkFtZTcwNTY1ODg3OA@@._V1_SY317_CR5,0,214,317_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'William Munny (Clint Eastwood) es un pistolero retirado, viudo y padre de familia, que tiene dificultades económicas para sacar adelante a su hijos. Su única salida es hacer un último trabajo. En compañía de un viejo colega (Morgan Freeman) y de un joven inexperto (Jaimz Woolvett), Munny tendrá que matar a dos hombres que cortaron la cara a una prostituta.'
		),
		array(
			'title' => 'El precio del poder',
			'year' => '1983', 
			'director' => 'Brian De Palma', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMjAzOTM4MzEwNl5BMl5BanBnXkFtZTgwMzU1OTc1MDE@._V1_SX214_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Tony Montana es un emigrante cubano frío y sanguinario que se instala en Miami con el propósito de convertirse en un gángster importante. Con la colaboración de su amigo Manny Rivera inicia una fulgurante carrera delictiva con el objetivo de acceder a la cúpula de una organización de narcos.'
		),
		array(
			'title' => 'El pianista',
			'year' => '2002', 
			'director' => 'Roman Polanski', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTc4OTkyOTA3OF5BMl5BanBnXkFtZTYwMDIxNjk5._V1_SX214_AL_.jpg', 
			'rented' => true, 
			'synopsis' => 'Wladyslaw Szpilman, un brillante pianista polaco de origen judío, vive con su familia en el ghetto de Varsovia. Cuando, en 1939, los alemanes invaden Polonia, consigue evitar la deportación gracias a la ayuda de algunos amigos. Pero tendrá que vivir escondido y completamente aislado durante mucho tiempo, y para sobrevivir tendrá que afrontar constantes peligros.'
		),
		array(
			'title' => 'Seven',
			'year' => '1995', 
			'director' => 'David Fincher', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTQwNTU3MTE4NF5BMl5BanBnXkFtZTcwOTgxNDM2Mg@@._V1_SX214_AL_.jpg', 
			'rented' => true, 
			'synopsis' => 'El veterano teniente Somerset (Morgan Freeman), del departamento de homicidios, está a punto de jubilarse y ser reemplazado por el ambicioso e impulsivo detective David Mills (Brad Pitt). Ambos tendrán que colaborar en la resolución de una serie de asesinatos cometidos por un psicópata que toma como base la relación de los siete pecados capitales: gula, pereza, soberbia, avaricia, envidia, lujuria e ira. Los cuerpos de las víctimas, sobre los que el asesino se ensaña de manera impúdica, se convertirán para los policías en un enigma que les obligará a viajar al horror y la barbarie más absoluta.'
		),
		array(
			'title' => 'El silencio de los corderos',
			'year' => '1991', 
			'director' => 'Jonathan Demme', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTQ2NzkzMDI4OF5BMl5BanBnXkFtZTcwMDA0NzE1NA@@._V1_SX214_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'El FBI busca a "Buffalo Bill", un asesino en serie que mata a sus víctimas, todas adolescentes, después de prepararlas minuciosamente y arrancarles la piel. Para poder atraparlo recurren a Clarice Starling, una brillante licenciada universitaria, experta en conductas psicópatas, que aspira a formar parte del FBI. Siguiendo las instrucciones de su jefe, Jack Crawford, Clarice visita la cárcel de alta seguridad donde el gobierno mantiene encerrado a Hannibal Lecter, antiguo psicoanalista y asesino, dotado de una inteligencia superior a la normal. Su misión será intentar sacarle información sobre los patrones de conducta de "Buffalo Bill".'
		),
		array(
			'title' => 'La naranja mecánica',
			'year' => '1971', 
			'director' => 'Stanley Kubrick', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTY3MjM1Mzc4N15BMl5BanBnXkFtZTgwODM0NzAxMDE@._V1_SY317_CR0,0,214,317_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Gran Bretaña, en un futuro indeterminado. Alex (Malcolm McDowell) es un joven muy agresivo que tiene dos pasiones: la violencia desaforada y Beethoven. Es el jefe de la banda de los drugos, que dan rienda suelta a sus instintos más salvajes apaleando, violando y aterrorizando a la población. Cuando esa escalada de terror llega hasta el asesinato, Alex es detenido y, en prisión, se someterá voluntariamente a una innovadora experiencia de reeducación que pretende anular drásticamente cualquier atisbo de conducta antisocial.'
		),
		array(
			'title' => 'La chaqueta metálica',
			'year' => '1987', 
			'director' => 'Stanley Kubrick', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMjA4NzY4ODk4Nl5BMl5BanBnXkFtZTgwOTcxNTYxMTE@._V1_SX214_AL_.jpg', 
			'rented' => true, 
			'synopsis' => 'Un grupo de reclutas se prepara en Parish Island, centro de entrenamiento de la marina norteamericana. Allí está el sargento Hartman, duro e implacable, cuya única misión en la vida es endurecer el cuerpo y el alma de los novatos, para que puedan defenderse del enemigo. Pero no todos los jóvenes están preparados para soportar sus métodos. '
		),
		array(
			'title' => 'Blade Runner',
			'year' => '1982', 
			'director' => 'Ridley Scott', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTA4MDQxNTk2NDheQTJeQWpwZ15BbWU3MDE2NjIyODk@._V1_SX214_AL_.jpg', 
			'rented' => true, 
			'synopsis' => 'A principios del siglo XXI, la poderosa Tyrell Corporation creó, gracias a los avances de la ingeniería genética, un robot llamado Nexus 6, un ser virtualmente idéntico al hombre pero superior a él en fuerza y agilidad, al que se dio el nombre de Replicante. Estos robots trabajaban como esclavos en las colonias exteriores de la Tierra. Después de la sangrienta rebelión de un equipo de Nexus-6, los Replicantes fueron desterrados de la Tierra. Brigadas especiales de policía, los Blade Runners, tenían órdenes de matar a todos los que no hubieran acatado la condena. Pero a esto no se le llamaba ejecución, se le llamaba "retiro". '
		),
		array(
			'title' => 'Taxi Driver',
			'year' => '1976', 
			'director' => 'Martin Scorsese', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMTQ1Nzg3MDQwN15BMl5BanBnXkFtZTcwNDE2NDU2MQ@@._V1_SY317_CR9,0,214,317_AL_.jpg', 
			'rented' => false, 
			'synopsis' => 'Para sobrellevar el insomnio crónico que sufre desde su regreso de Vietnam, Travis Bickle (Robert De Niro) trabaja como taxista nocturno en Nueva York. Es un hombre insociable que apenas tiene contacto con los demás, se pasa los días en el cine y vive prendado de Betsy (Cybill Shepherd), una atractiva rubia que trabaja como voluntaria en una campaña política. Pero lo que realmente obsesiona a Travis es comprobar cómo la violencia, la sordidez y la desolación dominan la ciudad. Y un día decide pasar a la acción.'
		),
		array(
			'title' => 'El club de la lucha',
			'year' => '1999', 
			'director' => 'David Fincher', 
			'poster' => 'http://ia.media-imdb.com/images/M/MV5BMjIwNTYzMzE1M15BMl5BanBnXkFtZTcwOTE5Mzg3OA@@._V1_SX214_AL_.jpg', 
			'rented' => true, 
			'synopsis' => 'Un joven hastiado de su gris y monótona vida lucha contra el insomnio. En un viaje en avión conoce a un carismático vendedor de jabón que sostiene una teoría muy particular: el perfeccionismo es cosa de gentes débiles; sólo la autodestrucción hace que la vida merezca la pena. Ambos deciden entonces fundar un club secreto de lucha, donde poder descargar sus frustaciones y su ira, que tendrá un éxito arrollador.'
		)
	);


    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
		self::seedCatalog();
		$this->command->info('Tabla catálogo inicializada con datos!');
		
		self::seedUsers();
  		$this->command->info('Tabla usuarios inicializada con datos!');
    }
    
    private function seedUsers()
	{
		DB::table('users')->delete();
		$user1 = new User;
 		$user1->name = "usuario1";
 		$user1->email = "usuario1@email.com";
 		$user1->password = bcrypt( "usuario1" );
 		$user1->save();
 		
 		$user2 = new User;
 		$user2->name = "usuario2";
 		$user2->email = "usuario2@email.com";
 		$user2->password = bcrypt( "usuario2" );
 		$user2->save();
	}
	
	private function seedCatalog()
	{
		DB::table('movies')->delete();
		foreach( $this->arrayPeliculas as $pelicula ) 
		{
			$p = new Movie;
			$p->title = $pelicula['title'];
			$p->year = $pelicula['year'];
			$p->director = $pelicula['director'];
			$p->poster = $pelicula['poster'];
			$p->rented = $pelicula['rented'];
			$p->synopsis = $pelicula['synopsis'];
			$p->save();
		}	
	}
	
}
