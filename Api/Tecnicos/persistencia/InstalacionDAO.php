<?php 
class InstalacionDAO{
    private $id_instalacion;
    private $estado;
    private $fecha_r;
    private $fecha_ini_proceso;
    private $id_estado_instalacion; //Por el momento se tomara el 7 como proceso de instalacion, despues se tomara otros valores
    private $direccion;
    private $telefonos;
    private $nombre;
    private $id_usuario_tec;

    private $obs_instalacion;
    private $tecnico_ini;

    public function __construct($id_instalacion =0, $estado="", $fecha_r="", $fecha_ini_proceso="", $id_estado_instalacion=0, $direccion="", $telefonos="", $nombre="", $id_usuario_tec=0, $obs_instalacion=null, $tecnico_ini=null){
        $this->id_instalacion = $id_instalacion;
        $this->estado = $estado;
        $this->fecha_r = $fecha_r;
        $this->fecha_ini_proceso = $fecha_ini_proceso;
        $this->id_estado_instalacion = $id_estado_instalacion;
        $this->direccion = $direccion;
        $this->telefonos = $telefonos;
        $this->nombre = $nombre;
        $this->id_usuario_tec = $id_usuario_tec;
        $this->obs_instalacion = $obs_instalacion;
        $this->tecnico_ini = $tecnico_ini;
    }

    public function consultarPorBusqueda(){
        return "SELECT fecha_r, id_estado_instalacion,fecha_ini_proceso, direccion, telefonos, nombre
                FROM instalacion
                WHERE id_instalacion = ". $this->id_instalacion. " AND (id_estado_instalacion = ". $this->id_estado_instalacion ." OR id_estado_instalacion = 8 )
                ";
    }

    public function consultarPlanPorId(){
        return "SELECT t.id_tipo_servicio, t.tipo_servicio
                FROM instalacion as i JOIN plan as p ON (i.id_plan = p.id_plan)
                                 JOIN tipo_servicio as t ON (p.id_tipo_servicio = t.id_tipo_servicio)
                WHERE id_instalacion = ". $this->id_instalacion. "
            ";
    }

    public function cambiarEstado(){
        return "UPDATE instalacion
                SET id_estado_instalacion = ". $this->id_estado_instalacion. "
                WHERE id_instalacion = ". $this->id_instalacion. "
            ";
    }

    public function finalizarInstalacion(){
        return "UPDATE instalacion
                SET id_estado_instalacion = ".$this->id_estado_instalacion.", id_usuario_tec = ". $this->id_usuario_tec. ",  fecha_f = NOW()
                WHERE id_instalacion = ". $this->id_instalacion. "
            ";
    }

    public function iniciarProceso(){
        return "UPDATE instalacion
                SET fecha_ini_proceso = NOW(), id_tecnico_inicio = ".$this->tecnico_ini."
                WHERE id_instalacion = ". $this->id_instalacion. "
            ";
    }
}
?>