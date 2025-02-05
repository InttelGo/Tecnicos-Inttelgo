<?php

if(isset($_GET["id"])){
    $foto = new Foto(null, $_GET["id"]);
    $fotos = array();
    $fotos = $foto->consultarPorInstalacion();
    echo json_encode([
        "success" => true,
        "pictures" => array_map(function($f){
            return [
                "foto" => $f->getFoto(),
                "fecha" => $f->getFecha(),
                "ubicacion" => $f->getUbicacion(),
                "id_obs_ticket" => $f->getIdObsTicket()
            ];
        }, $fotos)
    ]);

}
?>