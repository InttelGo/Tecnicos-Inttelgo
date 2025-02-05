<?php
include_once(__DIR__."/../persistencia/Conexion.php");
include_once(__DIR__.'/../persistencia/InstalacionDAO.php');

class Instalacion{
    private $id_instalacion;
    private $estado;
    private $fecha_r;
    private $fecha_ini_Proceso;
    private $id_estado_instalacion;
    private $direccion;
    private $telefonos;
    private $nombre;
    private $id_usuario_tec;
    private $obs_instalacion;
    private $tecnico_ini;
    private $plan;
    
    public function getId_instalacion(){
        return $this->id_instalacion;
    }
    
    public function getEstado(){
        return $this->estado;
    }
    
    public function getFecha_r(){
        return $this->fecha_r;
    }
    
    public function getFecha_ini_Proceso(){
        return $this->fecha_ini_Proceso;
    }
    
    public function getId_estado_instalacion(){
        return $this->id_estado_instalacion;
    }
    
    public function getDireccion(){
        return $this->direccion;
    }
    
    public function getTelefonos(){
        return $this->telefonos;
    }
    
    public function getNombre(){
        return $this->nombre;
    }
    
    public function getId_usuario_tec(){
        return $this->id_usuario_tec;
    }
    
    public function getObs_instalacion(){
        return $this->obs_instalacion;
    }
    
    public function getTecnico_ini(){
        return $this->tecnico_ini;
    }
    
    public function getPlan(){
        return $this->plan;
    }
    
    public function setId_instalacion($id_instalacion){
        $this->id_instalacion = $id_instalacion;
    }
    
    public function setEstado($estado){
        $this->estado = $estado;
    }
    
    public function setFecha_r($fecha_r){
        $this->fecha_r = $fecha_r;
    }
    
    public function setFecha_ini_Proceso($fecha_ini_Proceso){
        $this->fecha_ini_Proceso = $fecha_ini_Proceso;
    }
    
    public function setId_estado_instalacion($id_estado_instalacion){
        $this->id_estado_instalacion = $id_estado_instalacion;
    }
    
    public function setDireccion($direccion){
        $this->direccion = $direccion;
    }
    
    public function setTelefonos($telefonos){
        $this->telefonos = $telefonos;
    }
    
    public function setNombre($nombre){
        $this->nombre = $nombre;
    }
    
    public function setId_usuario_tec($id_usuario_tec){
        $this->id_usuario_tec = $id_usuario_tec;
    }
    
    public function setObs_instalacion($obs_instalacion){
        $this->obs_instalacion = $obs_instalacion;
    }
    
    public function setPlan($plan){
        $this->plan = $plan;
    }

    public function setTecnico_ini($tecnico_ini){
        $this->tecnico_ini = $tecnico_ini;
    }



    public function __construct($id_instalacion =0, $estado="", $fecha_r="", $fecha_ini_Proceso="", $id_estado_instalacion=7, $direccion="", $telefonos="", $nombre="", $id_usuario_tec=0, $obs_instalacion=null, $plan = null, $tecnico_ini=null){
        $this->id_instalacion = $id_instalacion;
        $this->estado = $estado;
        $this->fecha_r = $fecha_r;
        $this->fecha_ini_Proceso = $fecha_ini_Proceso;
        $this->id_estado_instalacion = $id_estado_instalacion;
        $this->direccion = $direccion;
        $this->telefonos = $telefonos;
        $this->nombre = $nombre;
        $this->id_usuario_tec = $id_usuario_tec;
        $this->obs_instalacion = $obs_instalacion;
        $this->plan = $plan;
        $this->tecnico_ini = $tecnico_ini;
    }

    public function consultarPorBusqueda(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $instalacionDAO = new InstalacionDAO($this->id_instalacion, null, null,null, $this->id_estado_instalacion);
        $conexion->ejecutarConsulta($instalacionDAO->consultarPorBusqueda());
        if($conexion->numeroFilas() > 0 ){
            $registro = $conexion->siguienteRegistro();
            $this->fecha_r = $registro[0];
            $this->id_estado_instalacion = $registro[1];
            $this->fecha_ini_Proceso = $registro[2];
            $this->direccion = $registro[3];
            $this->telefonos = $registro[4];
            $this->nombre = $registro[5];
            $this->obs_instalacion = new Observacion($this->id_instalacion);
            $this->obs_instalacion->consultarPorUltimoId();
            $conexion->cerrarConexion();
            return true;
        }
        $conexion->cerrarConexion();
        return false;
    }

    public function consultarPlanPorId(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $instalacionDAO = new InstalacionDAO($this->id_instalacion);
        $conexion->ejecutarConsulta($instalacionDAO->consultarPlanPorId());
        $registro = $conexion->siguienteRegistro();
        $this->plan = new Plan($registro[0], $registro[1]);
        $conexion->cerrarConexion();
    }

    public function cambiarEstado(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $instalacionDAO = new InstalacionDAO($this->id_instalacion, null, null,$this->fecha_ini_Proceso, $this->id_estado_instalacion);
        $conexion->ejecutarConsulta($instalacionDAO->cambiarEstado());
        $conexion->cerrarConexion();
        return true;
    }

    public function finalizarInstalacion(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $instalacionDAO = new InstalacionDAO($this->id_instalacion, null,null,null,  
        $this->id_estado_instalacion, null, null, 
        null, $this->id_usuario_tec);
        $conexion->ejecutarConsulta($instalacionDAO->finalizarInstalacion());
        $num = $this->id_instalacion;
        $conexion->cerrarConexion();
        return $num;
    }

    public function iniciarProceso(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $instalacionDAO = new InstalacionDAO($this->id_instalacion, null, null,$this->fecha_ini_Proceso, null,null,null,null,null,null,$this->tecnico_ini);
        $conexion->ejecutarConsulta($instalacionDAO->iniciarProceso());
        $conexion->cerrarConexion();
        return true;
    }
}
?>