<?php 
if(isset($_GET["search"])){
    $instalacion = new Instalacion($_GET['search']);
        // Llamar al método consultar y verificar el resultado
        if ($instalacion->consultarPorBusqueda()) {
            echo json_encode([
                "success" => true,
                "proceso" => [
                    "id_instalacion"=> $instalacion->getId_instalacion(),
                    "fecha_r" => $instalacion->getFecha_r(),
                    "fecha_ini" => $instalacion->getFecha_ini_Proceso(),
                    "id_estado_instalacion"=> $instalacion->getId_estado_instalacion(),
                    "direccion"=> $instalacion->getDireccion(),
                    "telefonos"=> $instalacion->getTelefonos(),
                    "nombre"=> $instalacion->getNombre(),
                    "observacion" => [
                        "id" => $instalacion->getObs_instalacion()->getId(),
                        "descripcion" => $instalacion->getObs_instalacion()->getDescripcion(),
                    ]
                ]
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "No se encontro el resultado en la base de datos"
            ]);
        }
}else if(isset($_GET["id"])){
    if(isset($_GET["estado"])){
        $instalacion = new Instalacion($_GET["id"], null, null, $_GET["fecha_ini"], $_GET["estado"], null, null, null,null,null,null, $_GET["id_tec_ini"]);
        if ( $instalacion->getFecha_ini_Proceso() == "null"){
            $instalacion->iniciarProceso();
        }
        echo $instalacion->cambiarEstado();
    }else{
        $instalacion = new Instalacion($_GET["id"]);
        $instalacion->consultarPlanPorId();
        echo json_encode([
            "success" => true,
            "plan" => [
                "id_plan" => $instalacion->getPlan()->getId_plan(),
                "descripcion" => $instalacion->getPlan()->getDescripcion()
            ]
        ]);
    }
}else if(isset($_GET["idT"])){
    $instalacion = new Instalacion($_GET["idT"], null, null, null, 3, 
    null, null, null, 
    $_GET["idTec"]);
    $obs = new Obs_ticket(null,$_GET["obs"], $_GET["idT"], $_GET["date"]);
    $obs->guardarI($_GET["idTec"]);
    echo $instalacion->finalizarInstalacion();
}

?>
