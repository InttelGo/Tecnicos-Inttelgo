<?php
session_start();
if(isset($_GET["cerrarSesion"])){
    session_destroy();
}

error_reporting(E_ALL); // Muestra todos los errores
ini_set('display_errors', 1); // Habilita la visualización de errores

require("logic/Usuario.php");
require("logic/Tecnico.php");
require("logic/Instalacion.php");
require("logic/observacion.php"); 
require("logic/Ticket.php");
require("logic/Tipo.php");
require("logic/Cliente.php");
require("logic/Obs_ticket.php");
require("logic/Picture.php");
require("logic/Foto.php");
require("logic/Barrio.php");
require("logic/Plan.php");
require("logic/Articulo.php");

$paginasSinSession = array(
    "pages/iniciarSesion.php",
    "pages/process.php",
    "pages/ticket.php",
    "pages/obs_ticket.php",
    "pages/upload.php",
    "pages/barrio.php",
    "pages/articulo.php",
    "pages/ubication.php",
    "pages/image.php"
);

//echo base64_encode("pages/iniciarSesion.php");

if(isset($_GET["pid"])){
    $pid = base64_decode($_GET["pid"]);
    if(in_array($pid, $paginasSinSession)){ 
        include ($pid);
    }else {
        echo "<h1>Error 404</h1>";   
    }
}
?>