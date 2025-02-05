<?php 
class ClienteDAO{
    private $id_cliente;
    private $nombre_1;
    private $apellido_1;
    private $telefono_1;
    private $telefono_2;
    private $direccion;

    public function __construct($id_cliente=0, $nombre_1="", $apellido_1="", $telefono_1="", $telefono_2="", $direccion=""){
        $this->id_cliente = $id_cliente;
        $this->nombre_1 = $nombre_1;
        $this->apellido_1 = $apellido_1;
        $this->telefono_1 = $telefono_1;
        $this->telefono_2 = $telefono_2;
        $this->direccion = $direccion;
    }

    public function consultarPorId(){
        return "SELECT CONCAT(red, prefijo, '0',num_cliente) as nroCliente, nombre_1, apellido_1, telefono_1, telefono_2, direccion
                FROM cliente JOIN barrio ON (cliente.id_barrio = barrio.id_barrio)
                WHERE id_cliente = ".$this->id_cliente."
        ";
    }

    public function consultarPorIdConBarrio(){
        return "SELECT CONCAT(red, prefijo, '0',num_cliente) as nroCliente, nombre_1, apellido_1, telefono_1, telefono_2, direccion, prefijo
                FROM cliente JOIN barrio ON (cliente.id_barrio = barrio.id_barrio)
                WHERE id_cliente = ".$this->id_cliente."
        ";
    } 
}
?>