<?php 
include_once (__DIR__."/../persistencia/Conexion.php"); 
include_once (__DIR__."/../persistencia/PictureDAO.php"); 
class Picture{
    private $fecha;
    private $ubicacion;
    private $foto;
    private $id_obs_ticket;

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
    
    public function setFoto($foto){
        $this->foto = $foto;
    }
    
    public function getFoto(){
        return $this->foto;
    }
    
    public function setId_obs_ticket($id_obs_ticket){
        $this->id_obs_ticket = $id_obs_ticket;
    }
    
    public function getId_obs_ticket(){
        return $this->id_obs_ticket;
    }
    
    public function __construct($id_obs_ticket=null, $fecha=null, $ubicacion=null, $foto=null){
        $this->fecha = $fecha;
        $this->ubicacion = $ubicacion;
        $this->id_obs_ticket = $id_obs_ticket;
        $this->foto = $foto;
    }

    public function consultarpictures(){
        $pictures = array();
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $pictureDAO = new PictureDAO($this->id_obs_ticket);
        $conexion->ejecutarConsulta($pictureDAO->consultarPictures());
        while($resultado = $conexion->siguienteRegistro()){
            $p = new Picture($this->id_obs_ticket, $resultado[0], $resultado[1], $resultado[2]);
            array_push($pictures, $p);
        }
        $conexion->cerrarConexion();
        return $pictures;
    }

}
?>