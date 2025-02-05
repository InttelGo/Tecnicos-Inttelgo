<?php 
include_once (__DIR__."/../persistencia/Conexion.php"); 
include_once (__DIR__."/../persistencia/FotoDAO.php"); 
class Foto{
    private $foto;
    private $id_obs_ticket;
    private $fecha;
    private $ubicacion;

    public function setFoto($foto){
        $this->foto = $foto;
    }

    public function getFoto(){
        return $this->foto;
    }
    
    public function setIdObsTicket($id_obs_ticket){
        $this->id_obs_ticket = $id_obs_ticket;
    }
    
    public function getIdObsTicket(){
        return $this->id_obs_ticket;
    }
    
    public function setFecha($fecha){
        $this->fecha = $fecha;
    }
    
    public function getFecha(){
        return $this->fecha;
    }
    
    public function setUbicacion($ubicacion){
        $this->ubicacion = $ubicacion;
    }
    
    public function getUbicacion(){
        return $this->ubicacion;
    }
    

    public function __construct($foto="", $id_obs_ticket=0, $fecha="", $ubicacion=null){
        $this->foto = $foto;
        $this->id_obs_ticket = $id_obs_ticket;
        $this->fecha = $fecha;
        $this->ubicacion = $ubicacion;
    }

    public function guardar(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $fotoDAO = new FotoDAO($this->foto,$this->id_obs_ticket,$this->fecha, $this->ubicacion);
        $conexion->ejecutarConsulta($fotoDAO->guardar());
        $conexion->cerrarConexion();
    }

    public function guardarI($idIn){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $fotoDAO = new FotoDAO($this->foto,$this->id_obs_ticket,$this->fecha, $this->ubicacion);
        $conexion->ejecutarConsulta($fotoDAO->guardarI($idIn));
        $conexion->cerrarConexion();
    }

    public function consultarPorInstalacion(){
        $conexion = new Conexion();
        $fotos = array();
        $conexion->abrirConexion();
        $fotoDAO = new FotoDAO(null,$this->id_obs_ticket);
        $conexion->ejecutarConsulta($fotoDAO->consultarPorInstalacion());
        while ($respuesta = $conexion->siguienteRegistro()) {
            $foto = new Foto($respuesta[0], $this->id_obs_ticket, $respuesta[1], $respuesta[2]);
            array_push($fotos, $foto);
        }
        $conexion->cerrarConexion();
        return $fotos;
    }
}
?>