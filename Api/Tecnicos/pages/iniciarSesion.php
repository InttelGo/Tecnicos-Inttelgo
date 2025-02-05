<?php

if(isset($_SESSION["id"])){
    $tecnico = new Tecnico(null,null,$_SESSION["id"]);
    if($tecnico->consultarPorId()){
        echo json_encode([
            "sucess" => true,
            "data" => [
                "id_usuario" => $tecnico->getId_usuario(),
                "nombre_1" => $tecnico->getNombre_1(),
                "apellido_1" => $tecnico->getApellido_1(),
            ]
            ]);
    }else{
        echo json_encode([
            "sucess" => false,
            "message" => "No se pudo encontrar el técnico."
        ]);
    }
}else{
    // Validar si los parámetros username y password existen en la solicitud
    if (isset($_GET['username']) && isset($_GET['password'])) {
        // Crear instancia del objeto Técnico
        $tecnico = new Tecnico($_GET['username'], $_GET['password']);

        // Llamar al método consultar y verificar el resultado
        if ($tecnico->consultar()) {
            $_SESSION["id"] = $tecnico -> getId_usuario();
            // Respuesta en caso de éxito
            echo json_encode([
                "success" => true,
                "data" => [
                    "id_usuario" => $tecnico->getId_usuario(),
                    "color" => $tecnico->getColor()
                ]
            ]);
        } else {
            // Respuesta en caso de no encontrar registros
            echo json_encode([
                "success" => false,
                "message" => "No se encontraron registros para el usuario proporcionado."
            ]);
        }
    }else if(isset($_GET["id"])){
        $tecnico = new Tecnico(null,null,$_GET["id"]);
        if($tecnico->consultarPorId()){
            echo json_encode([
                "success" => true,
                "data" => [
                    "id_usuario" => $tecnico->getId_usuario(),
                    "nombre_1" => $tecnico->getNombre_1(),
                    "apellido_1" => $tecnico->getApellido_1(),
                ]
                ]);
        }else{
            echo json_encode([
                "success" => false,
                "message" => "No se pudo encontrar el técnico."
            ]);
        }
    } else {
        // Respuesta en caso de parámetros faltantes
        echo json_encode([
            "success" => false,
            "message" => "Los parámetros 'username' y 'password' son obligatorios."
        ]);
    }
}
?>
