//
//  MasterViewController.m
//  pelis
//
//  Created by Antonio Pertusa on 31/1/15.
//  Copyright (c) 2015 DLSI. All rights reserved.
//

#import "MasterViewController.h"
#import "DetailViewController.h"
#import "Peli.h"

@interface MasterViewController ()

@property NSMutableArray *pelis;
@end

@implementation MasterViewController

- (void)awakeFromNib {
    [super awakeFromNib];
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        self.clearsSelectionOnViewWillAppear = NO;
        self.preferredContentSize = CGSizeMake(320.0, 600.0);
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.

    [self createPelis];
    self.downloadingImages=[[NSMutableDictionary alloc] init];
    
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    // Se pueden hacer cambios en la configuración, por ejemplo:
    config.allowsCellularAccess = NO;
    
    // Creamos la sesión con esta configuración
    self.session = [NSURLSession sessionWithConfiguration:config];
    
/*
    self.navigationItem.leftBarButtonItem = self.editButtonItem;

    UIBarButtonItem *addButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(insertNewObject:)];
    self.navigationItem.rightBarButtonItem = addButton;
    self.detailViewController = (DetailViewController *)[[self.splitViewController.viewControllers lastObject] topViewController];
 */
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
- (void)insertNewObject:(id)sender {
    if (!self.objects) {
        self.objects = [[NSMutableArray alloc] init];
    }
    [self.objects insertObject:[NSDate date] atIndex:0];
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:0 inSection:0];
    [self.tableView insertRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
}
*/

#pragma mark - Segues

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"showDetail"]) {
        NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
        Peli *object = self.pelis[indexPath.row];
        DetailViewController *controller = (DetailViewController *)[[segue destinationViewController] topViewController];
        [controller setDetailItem:object];
        controller.navigationItem.leftBarButtonItem = self.splitViewController.displayModeButtonItem;
        controller.navigationItem.leftItemsSupplementBackButton = YES;
    }
}

#pragma mark - Table View

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.pelis.count;
}

- (void) cargarImagen: (Peli *) item
        paraIndexPath: (NSIndexPath *) indexPath
{
    if ([self.downloadingImages objectForKey: indexPath] == nil)
    {
        UAImageDownloader *theDownloader = [[UAImageDownloader alloc] initWithUrl:item.urlPoster indexPath:indexPath session:self.session delegate:self];
        [self.downloadingImages setObject: theDownloader forKey: indexPath];
    }
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell"];

    if (cell==nil)
    {
        cell=[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"Cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }

    Peli *object = self.pelis[indexPath.row];
    
    cell.textLabel.text = [object title];
    
    [cell.imageView setContentMode:UIViewContentModeScaleAspectFit];
    
    if(object.image!=nil) {
        cell.imageView.image = object.image;
    }
    else if(object.urlPoster!=nil)
    {
        
        cell.imageView.image = [UIImage imageNamed: @"Placeholder.png"];
        if (self.tableView.dragging == NO && self.tableView.decelerating == NO)
        {
            [self cargarImagen:object paraIndexPath: indexPath];
            NSLog(@"Entro");
        }
    }
    
    //cell.imageView.image=[UIImage imageNamed:@"Placeholder.png"];
    
    return cell;
}



- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return NO;
}

/*
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [self.objects removeObjectAtIndex:indexPath.row];
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view.
    }
}
*/


- (void) imageDownloader:(UAImageDownloader *)downloader
didFinishDownloadingImage:(UIImage *)image
            forIndexPath:(NSIndexPath *)indexPath
{
    Peli *theItem = [self.pelis objectAtIndex: indexPath.row];
    theItem.image = image;
    
    UITableViewCell *cell = [self.tableView cellForRowAtIndexPath: indexPath];
    cell.imageView.image = image;
    [cell layoutSubviews];
}

-(void)imageDownloader:(UAImageDownloader *)downloader
didFailDownloadingImage: (NSString *)url
          forIndexPath: (NSIndexPath *)indexPath
{
    Peli *theItem = [self.pelis objectAtIndex: indexPath.row];
    theItem.image=[UIImage imageNamed:@"not-found.png"];
    
    UITableViewCell *cell = [self.tableView cellForRowAtIndexPath: indexPath];
    cell.imageView.image = theItem.image;
    [cell layoutSubviews];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    if (!decelerate)
        [self cargarImagenesEnPantalla];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    [self cargarImagenesEnPantalla];
}

- (void)cargarImagenesEnPantalla
{
    if ([self.pelis count] > 0)
    {
        NSArray *visiblePaths = [self.tableView indexPathsForVisibleRows];
        for (NSIndexPath *indexPath in visiblePaths) {
            Peli *theItem = [self.pelis objectAtIndex:indexPath.row];
            
            if (!theItem.image)
                [self cargarImagen: theItem paraIndexPath: indexPath];
        }
    }
}


-(void)createPelis
{
    self.pelis=[[NSMutableArray alloc] init];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"El padrino"
                                             withYear:1972
                                         withDirector:@"Francis Ford Coppola" withPoster:@"http://ia.media-imdb.com/images/M/MV5BMjEyMjcyNDI4MF5BMl5BanBnXkFtZTcwMDA5Mzg3OA@@._V1_SX214_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"Don Vito Corleone (Marlon Brando) es el respetado y temido jefe de una de las cinco familias de la mafia de Nueva York. Tiene cuatro hijos: Connie (Talia Shire), el impulsivo Sonny (James Caan), el pusilánime Freddie (John Cazale) y Michael (Al Pacino), que no quiere saber nada de los negocios de su padre. Cuando Corleone, en contra de los consejos de \'Il consigliere\' Tom Hagen (Robert Duvall), se niega a intervenir en el negocio de las drogas, el jefe de otra banda ordena su asesinato. Empieza entonces una violenta y cruenta guerra entre las familias mafiosas."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"El Padrino. Parte II"
                                             withYear:1974
                                         withDirector:@"Francis Ford Coppola"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BNDc2NTM3MzU1Nl5BMl5BanBnXkFtZTcwMTA5Mzg3OA@@._V1_SX214_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"Continuación de la historia de los Corleone por medio de dos historias paralelas: la elección de Michael Corleone como jefe de los negocios familiares y los orígenes del patriarca, el ya fallecido Don Vito, primero en Sicilia y luego en Estados Unidos, donde, empezando desde abajo, llegó a ser un poderosísimo jefe de la mafia de Nueva York."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"La lista de Schindler"
                                             withYear:1993
                                         withDirector:@"Steven Spielberg"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMzMwMTM4MDU2N15BMl5BanBnXkFtZTgwMzQ0MjMxMDE@._V1_SX214_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"Segunda Guerra Mundial (1939-1945). Oskar Schindler (Liam Neeson), un hombre de enorme astucia y talento para las relaciones públicas, organiza un ambicioso plan para ganarse la simpatía de los nazis. Después de la invasión de Polonia por los alemanes (1939), consigue, gracias a sus relaciones con los nazis, la propiedad de una fábrica de Cracovia. Allí emplea a cientos de operarios judíos, cuya explotación le hace prosperar rápidamente. Su gerente (Ben Kingsley), también judío, es el verdadero director en la sombra, pues Schindler carece completamente de conocimientos para dirigir una empresa."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"Pulp Fiction"
                                             withYear:1994
                                         withDirector:@"Quentin Tarantino"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMjE0ODk2NjczOV5BMl5BanBnXkFtZTYwNDQ0NDg4._V1_SY317_CR4,0,214,317_AL_.jpg"
                                               rented:true
                                         withSynopsis:@"Jules y Vincent, dos asesinos a sueldo con muy pocas luces, trabajan para Marsellus Wallace. Vincent le confiesa a Jules que Marsellus le ha pedido que cuide de Mia, su mujer. Jules le recomienda prudencia porque es muy peligroso sobrepasarse con la novia del jefe. Cuando llega la hora de trabajar, ambos deben ponerse manos a la obra. Su misión: recuperar un misterioso maletín. "]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"Cadena perpetua"
                                             withYear:1994
                                         withDirector:@"Frank Darabont"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BODU4MjU4NjIwNl5BMl5BanBnXkFtZTgwMDU2MjEyMDE@._V1_SX214_AL_.jpg"
                                               rented:true
                                         withSynopsis:@"Acusado del asesinato de su mujer, Andrew Dufresne (Tim Robbins), tras ser condenado a cadena perpetua, es enviado a la cárcel de Shawshank. Con el paso de los años conseguirá ganarse la confianza del director del centro y el respeto de sus compañeros de prisión, especialmente de Red (Morgan Freeman), el jefe de la mafia de los sobornos."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"El golpe"
                                             withYear:1973
                                         withDirector:@"George Roy Hill"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTY5MjM1OTAyOV5BMl5BanBnXkFtZTgwMDkwODg4MDE@._V1._CR52,57,915,1388_SX214_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"Chicago, años treinta. Redford y Newman son dos timadores que deciden vengar la muerte de un viejo y querido colega, asesinado por orden de un poderoso gángster (Robert Shaw). Para ello urdirán un ingenioso y complicado plan con la ayuda de todos sus amigos y conocidos."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"La vida es bella"
                                             withYear:1997
                                         withDirector:@"Roberto Benigni"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTQwMTM2MjE4Ml5BMl5BanBnXkFtZTgwODQ2NTYxMTE@._V1_SX214_AL_.jpg"
                                               rented:true
                                         withSynopsis:@"En 1939, a punto de estallar la Segunda Guerra Mundial (1939-1945), el extravagante Guido llega a Arezzo (Toscana) con la intención de abrir una librería. Allí conoce a Dora y, a pesar de que es la prometida del fascista Ferruccio, se casa con ella y tiene un hijo. Al estallar la guerra, los tres son internados en un campo de exterminio, donde Guido hará lo imposible para hacer creer a su hijo que la terrible situación que están padeciendo es tan sólo un juego."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"Uno de los nuestros"
                                             withYear:1990
                                         withDirector:@"Martin Scorsese"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTY2OTE5MzQ3MV5BMl5BanBnXkFtZTgwMTY2NTYxMTE@._V1_SX214_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"Henry Hill, hijo de padre irlandés y madre siciliana, vive en Brooklyn y se siente fascinado por la vida que llevan los gángsters de su barrio, donde la mayoría de los vecinos son inmigrantes. Paul Cicero, el patriarca de la familia Pauline, es el protector del barrio. A los trece años, Henry decide abandonar la escuela y entrar a formar parte de la organización mafiosa como chico de los recados; muy pronto se gana la confianza de sus jefes, gracias a lo cual irá subiendo de categoría."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"Alguien voló sobre el nido del cuco"
                                             withYear:1975
                                         withDirector:@"Milos Forman"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTk5OTA4NTc0NF5BMl5BanBnXkFtZTcwNzI5Mzg3OA@@._V1_SY317_CR12,0,214,317_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"Randle McMurphy (Jack Nicholson), un hombre condenado por asalto, y un espíritu libre que vive contracorriente, es recluido en un hospital psiquiátrico. La inflexible disciplina del centro acentúa su contagiosa tendencia al desorden, que acabará desencadenando una guerra entre los pacientes y el personal de la clínica con la fría y severa enfermera Ratched (Louise Fletcher) a la cabeza. La suerte de cada paciente del pabellón está en juego."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"American History X"
                                             withYear:1998
                                         withDirector:@"Tony Kaye"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMjMzNDUwNTIyMF5BMl5BanBnXkFtZTcwNjMwNDg3OA@@._V1_SY317_CR17,0,214,317_AL_.jpg"
                                               rented:false
                                         withSynopsis:@" Derek (Edward Norton), un joven \"skin head\" californiano de ideología neonazi, fue encarcelado por asesinar a un negro que pretendía robarle su furgoneta. Cuando sale de prisión y regresa a su barrio dispuesto a alejarse del mundo de la violencia, se encuentra con que su hermano pequeño (Edward Furlong), para quien Derek es el modelo a seguir, sigue el mismo camino que a él lo condujo a la cárcel."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"Sin perdón"
                                             withYear:1992
                                         withDirector:@"Clint Eastwood"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTkzNTc0NDc4OF5BMl5BanBnXkFtZTcwNTY1ODg3OA@@._V1_SY317_CR5,0,214,317_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"William Munny (Clint Eastwood) es un pistolero retirado, viudo y padre de familia, que tiene dificultades económicas para sacar adelante a su hijos. Su única salida es hacer un último trabajo. En compañía de un viejo colega (Morgan Freeman) y de un joven inexperto (Jaimz Woolvett), Munny tendrá que matar a dos hombres que cortaron la cara a una prostituta."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"El precio del poder"
                                             withYear:1983
                                         withDirector:@"Brian De Palma"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMjAzOTM4MzEwNl5BMl5BanBnXkFtZTgwMzU1OTc1MDE@._V1_SX214_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"Tony Montana es un emigrante cubano frío y sanguinario que se instala en Miami con el propósito de convertirse en un gángster importante. Con la colaboración de su amigo Manny Rivera inicia una fulgurante carrera delictiva con el objetivo de acceder a la cúpula de una organización de narcos."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"El pianista"
                                             withYear:2002
                                         withDirector:@"Roman Polanski"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTc4OTkyOTA3OF5BMl5BanBnXkFtZTYwMDIxNjk5._V1_SX214_AL_.jpg"
                                               rented:true
                                         withSynopsis:@"Wladyslaw Szpilman, un brillante pianista polaco de origen judío, vive con su familia en el ghetto de Varsovia. Cuando, en 1939, los alemanes invaden Polonia, consigue evitar la deportación gracias a la ayuda de algunos amigos. Pero tendrá que vivir escondido y completamente aislado durante mucho tiempo, y para sobrevivir tendrá que afrontar constantes peligros."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"Seven"
                                             withYear:1995
                                         withDirector:@"David Fincher"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTQwNTU3MTE4NF5BMl5BanBnXkFtZTcwOTgxNDM2Mg@@._V1_SX214_AL_.jpg"
                                               rented:true
                                         withSynopsis:@"El veterano teniente Somerset (Morgan Freeman), del departamento de homicidios, está a punto de jubilarse y ser reemplazado por el ambicioso e impulsivo detective David Mills (Brad Pitt). Ambos tendrán que colaborar en la resolución de una serie de asesinatos cometidos por un psicópata que toma como base la relación de los siete pecados capitales: gula, pereza, soberbia, avaricia, envidia, lujuria e ira. Los cuerpos de las víctimas, sobre los que el asesino se ensaña de manera impúdica, se convertirán para los policías en un enigma que les obligará a viajar al horror y la barbarie más absoluta."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"El silencio de los corderos"
                                             withYear:1991
                                         withDirector:@"Jonathan Demme"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTQ2NzkzMDI4OF5BMl5BanBnXkFtZTcwMDA0NzE1NA@@._V1_SX214_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"El FBI busca a \"Buffalo Bill\", un asesino en serie que mata a sus víctimas, todas adolescentes, después de prepararlas minuciosamente y arrancarles la piel. Para poder atraparlo recurren a Clarice Starling, una brillante licenciada universitaria, experta en conductas psicópatas, que aspira a formar parte del FBI. Siguiendo las instrucciones de su jefe, Jack Crawford, Clarice visita la cárcel de alta seguridad donde el gobierno mantiene encerrado a Hannibal Lecter, antiguo psicoanalista y asesino, dotado de una inteligencia superior a la normal. Su misión será intentar sacarle información sobre los patrones de conducta de \"Buffalo Bill\"."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"La naranja mecánica"
                                             withYear:1971
                                         withDirector:@"Stanley Kubrick"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTY3MjM1Mzc4N15BMl5BanBnXkFtZTgwODM0NzAxMDE@._V1_SY317_CR0,0,214,317_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"Gran Bretaña, en un futuro indeterminado. Alex (Malcolm McDowell) es un joven muy agresivo que tiene dos pasiones: la violencia desaforada y Beethoven. Es el jefe de la banda de los drugos, que dan rienda suelta a sus instintos más salvajes apaleando, violando y aterrorizando a la población. Cuando esa escalada de terror llega hasta el asesinato, Alex es detenido y, en prisión, se someterá voluntariamente a una innovadora experiencia de reeducación que pretende anular drásticamente cualquier atisbo de conducta antisocial."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"La chaqueta metálica"
                                             withYear:1987
                                         withDirector:@"Stanley Kubrick"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMjA4NzY4ODk4Nl5BMl5BanBnXkFtZTgwOTcxNTYxMTE@._V1_SX214_AL_.jpg"
                                               rented:true
                                         withSynopsis:@"Un grupo de reclutas se prepara en Parish Island, centro de entrenamiento de la marina norteamericana. Allí está el sargento Hartman, duro e implacable, cuya única misión en la vida es endurecer el cuerpo y el alma de los novatos, para que puedan defenderse del enemigo. Pero no todos los jóvenes están preparados para soportar sus métodos."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"Blade Runner"
                                             withYear:1982
                                         withDirector:@"Ridley Scott"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTA4MDQxNTk2NDheQTJeQWpwZ15BbWU3MDE2NjIyODk@._V1_SX214_AL_.jpg"
                                               rented:true
                                         withSynopsis:@"A principios del siglo XXI, la poderosa Tyrell Corporation creó, gracias a los avances de la ingeniería genética, un robot llamado Nexus 6, un ser virtualmente idéntico al hombre pero superior a él en fuerza y agilidad, al que se dio el nombre de Replicante. Estos robots trabajaban como esclavos en las colonias exteriores de la Tierra. Después de la sangrienta rebelión de un equipo de Nexus-6, los Replicantes fueron desterrados de la Tierra. Brigadas especiales de policía, los Blade Runners, tenían órdenes de matar a todos los que no hubieran acatado la condena. Pero a esto no se le llamaba ejecución, se le llamaba \"retiro\"."]];
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"Taxi Driver"
                                             withYear:1976
                                         withDirector:@"Martin Scorsese"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMTQ1Nzg3MDQwN15BMl5BanBnXkFtZTcwNDE2NDU2MQ@@._V1_SY317_CR9,0,214,317_AL_.jpg"
                                               rented:false
                                         withSynopsis:@"Para sobrellevar el insomnio crónico que sufre desde su regreso de Vietnam, Travis Bickle (Robert De Niro) trabaja como taxista nocturno en Nueva York. Es un hombre insociable que apenas tiene contacto con los demás, se pasa los días en el cine y vive prendado de Betsy (Cybill Shepherd), una atractiva rubia que trabaja como voluntaria en una campaña política. Pero lo que realmente obsesiona a Travis es comprobar cómo la violencia, la sordidez y la desolación dominan la ciudad. Y un día decide pasar a la acción."]];
    
    
    [self.pelis addObject:[[Peli alloc] initWithTitle:@"El club de la lucha"
                                             withYear:1999
                                         withDirector:@"David Fincher"
                                           withPoster:@"http://ia.media-imdb.com/images/M/MV5BMjIwNTYzMzE1M15BMl5BanBnXkFtZTcwOTE5Mzg3OA@@._V1_SX214_AL_.jpg"
                                               rented:true
                                         withSynopsis:@"Un joven hastiado de su gris y monótona vida lucha contra el insomnio. En un viaje en avión conoce a un carismático vendedor de jabón que sostiene una teoría muy particular: el perfeccionismo es cosa de gentes débiles; sólo la autodestrucción hace que la vida merezca la pena. Ambos deciden entonces fundar un club secreto de lucha, donde poder descargar sus frustaciones y su ira, que tendrá un éxito arrollador."]];
    
}


@end
