<?php
if(isset($_GET["id"])){  //Historial
    $obs = new Obs_ticket(null, null, $_GET["id"]);
    $historial = $obs->consultarHistorial();
    echo json_encode([
        "sucess" => true,
        "obs_ticket" => array_map(function($obs){
            return [
                "id_obs_ticket" => $obs->getId_obs_ticket(),
                "obs" => $obs->getObs(),
                "fecha" => $obs->getFecha(),
                "tecnico" =>[
                    "id_tecnico" => $obs->getId_user()->getId_usuario(),
                    "nombre" => $obs->getId_user()->getNombre_1(),
                    "apellido" => $obs->getId_user()->getApellido_1()
                ]
            ];
        },$historial)
    ]);
}
if(isset($_GET["idobs"])){ //Imagenes historial
    $picture = new Picture($_GET["idobs"]);
    $pictures = $picture->consultarpictures();

    // Convertir a JSON
    echo json_encode([
        "sucess" => true,
        "pictures" => array_map(function($pic){
            return [
                "id_obs_ticket" => $pic->getId_obs_ticket(),
                "foto" => $pic->getFoto(),
                "fecha" => $pic->getFecha(),
                "ubicacion" => $pic->getUbicacion(),
            ];
        },$pictures)
    ]);
}
if(isset($_GET["obs"])){ //Modificar Ticket
    $num_obs = 0;
    $obs = new Obs_ticket(null,$_GET["obs"], $_GET["idT"], $_GET["date"], $_GET["idTec"]);
    if($_GET["tipo"] == "Soporte"){
        $num_obs = $obs->guardar();
    }else if($_GET["tipo"] =="Finalizar"){
        $num_obs = $obs->guardarFinalizar($_GET["idTec"]);
    }
    echo $num_obs;
}
?>