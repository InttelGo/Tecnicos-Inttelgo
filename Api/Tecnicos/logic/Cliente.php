<?php 
include_once (__DIR__."/../persistencia/Conexion.php"); 
include_once (__DIR__."/../persistencia/ClienteDAO.php"); 
class Cliente{
    private $id_cliente;
    private $nombre_1;
    private $apellido_1;
    private $telefono_1;
    private $telefono_2;
    private $direccion;

    private $barrio;

    private $nroCliente;

    public function setId_cliente($id_cliente){
        $this->id_cliente = $id_cliente;
    }
    
    public function getId_cliente(){
        return $this->id_cliente;
    }
    
    public function setNombre_1($nombre_1){
        $this->nombre_1 = $nombre_1;
    }
    
    public function getNombre_1(){
        return $this->nombre_1;
    }
    
    public function setApellido_1($apellido_1){
        $this->apellido_1 = $apellido_1;
    }
    
    public function getApellido_1(){
        return $this->apellido_1;
    }
    
    public function setTelefono_1($telefono_1){
        $this->telefono_1 = $telefono_1;
    }
    
    public function getTelefono_1(){
        return $this->telefono_1;
    }
    
    public function setTelefono_2($telefono_2){
        $this->telefono_2 = $telefono_2;
    }
    
    public function getTelefono_2(){
        return $this->telefono_2;
    }
    
    public function setDireccion($direccion){
        $this->direccion = $direccion;
    }
    
    public function getDireccion(){
        return $this->direccion;
    }
    
    public function setBarrio($barrio){
        $this->barrio = $barrio;
    }
    
    public function getBarrio(){
        return $this->barrio;
    }

    public function setNroCliente($nro_cliente){
        $this->nroCliente = $nro_cliente;
    }
    
    public function getNroCliente(){
        return $this->nroCliente;
    }



    public function __construct($id_cliente=0, $nombre_1="", $apellido_1="", $telefono_1="", $telefono_2="", $direccion="", $barrio = null, $nro_cliente=""){
        $this->id_cliente = $id_cliente;
        $this->nombre_1 = $nombre_1;
        $this->apellido_1 = $apellido_1;
        $this->telefono_1 = $telefono_1;
        $this->telefono_2 = $telefono_2;
        $this->direccion = $direccion;
        $this->barrio = $barrio;
        $this->nroCliente = $nro_cliente;
    }

    public function consultarPorId(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $clienteDAO = new ClienteDAO($this->id_cliente);
        $conexion->ejecutarConsulta($clienteDAO->consultarPorId());
        if($conexion->numeroFilas() > 0){
            $resultado = $conexion->siguienteRegistro();
            $this->nroCliente = $resultado[0];
            $this->nombre_1 = $resultado[1];
            $this->apellido_1 = $resultado[2];
            $this->telefono_1 = $resultado[3];
            $this->telefono_2 = $resultado[4];
            $this->direccion = $resultado[5];
            $conexion->cerrarConexion();
            return true;
        }
        $conexion->cerrarConexion();
        return false;
    }

    public function consultarPorIdConBarrio(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $clienteDAO = new ClienteDAO($this->id_cliente);
        $conexion->ejecutarConsulta($clienteDAO->consultarPorIdConBarrio());
        if($conexion->numeroFilas() > 0){
            $resultado = $conexion->siguienteRegistro();
            $this->nroCliente = $resultado[0];
            $this->nombre_1 = $resultado[1];
            $this->apellido_1 = $resultado[2];
            $this->telefono_1 = $resultado[3];
            $this->telefono_2 = $resultado[4];
            $this->direccion = $resultado[5];
            $this->barrio = $resultado[6];
            $conexion->cerrarConexion();
            return true;
        }
        $conexion->cerrarConexion();
        return false;
    }

}
?>