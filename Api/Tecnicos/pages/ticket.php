<?php 
if(isset($_GET["prioridad"])){
    $ticket = new Ticket();
    $prioridad = $_GET["prioridad"];
    if($prioridad == 0){
        $tickets = $ticket->consultarTodos();
    }else{
        $tickets = $ticket->consultarPorPrioridad($prioridad);
    }

    echo json_encode([
        "success" => true,
        "tickets" => array_map(function($t) {
            return [
                "id_ticket" => $t->getId_ticket(),
                "tipo" => [
                    "id_tipo_ticket" => $t->getTipo()->getId_tipo(),
                    "descripcion" => $t->getTipo()->getDescripcion(),
                    "icon" => $t->getTipo()->getIcon(),
                ],
                "cliente" => [
                    "nroCliente" => $t->getCliente()->getNroCliente(),
                    "nombre_1" => $t->getCliente()->getNombre_1(),
                    "apellido_1" => $t->getCliente()->getApellido_1(),
                    "telefono_1" => $t->getCliente()->getTelefono_1(),
                    "telefono_2" => $t->getCliente()->getTelefono_2(),
                    "direccion" => $t->getCliente()->getDireccion(),
                    "barrio" => $t->getCliente()->getBarrio(),
                ],
                "observacion_u" => $t->getObservacion_u(),
                "fecha_hora" => $t->getFecha_hora()
            ];
        }, $tickets)
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
}
if(isset($_GET["id"])){
    $ticket = new Ticket($_GET["id"]);
    if($ticket->consultarPorId()){
        echo json_encode([
            "success" => true,
            "ticket" => [
                "id_ticket" => $ticket->getId_ticket(),
                "tipo" => [
                    "id_tipo_ticket" => $ticket->getTipo()->getId_tipo(),
                    "descripcion" => $ticket->getTipo()->getDescripcion()
                ],
                "cliente" => [
                    "nroCliente" => $ticket->getCliente()->getNroCliente(),
                    "nombre_1" => $ticket->getCliente()->getNombre_1(),
                    "apellido_1" => $ticket->getCliente()->getApellido_1(),
                    "telefono_1" => $ticket->getCliente()->getTelefono_1(),
                    "telefono_2" => $ticket ->getCliente()->getTelefono_2(),
                    "direccion" => $ticket->getCliente()->getDireccion(),
                ],
                "fecha_hora" => $ticket->getFecha_hora()
                ]
        ]);
    }else{
        echo json_encode([
            "success" => false,
            "message" => "Error"
        ]);
    }
}

if(isset($_GET["idArticle"])){
    $articulo = new Articulo($_GET["idArticle"], null, $_GET["cantidad"], date("Y-m-d H:i:s"),$_GET["idIn"]);
    $articulo->guardarArticulo();
}
?>