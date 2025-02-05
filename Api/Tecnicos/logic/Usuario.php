<?php 
include_once (__DIR__."/../persistencia/Conexion.php"); 
include_once (__DIR__."/../persistencia/TecnicoDAO.php"); 
class Usuario{
    protected $id_usuario;
    protected $nombre_1;
    protected $apellido_1;

    public function setId_usuario($id_usuario){
        $this->id_usuario = $id_usuario;
    }
    
    public function getId_usuario(){
        return $this->id_usuario;
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

    public function __construct($id_usuario=0, $nombre_1="", $apellido_1=""){
        $this->id_usuario = $id_usuario;
        $this->nombre_1 = $nombre_1;
        $this->apellido_1 = $apellido_1;
    }
}
?>