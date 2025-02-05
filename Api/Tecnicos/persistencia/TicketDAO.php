<?php
class TicketDAO{
    private $id_ticket;
    private $estado;
    private $tipo;
    private $cliente;
    private $observacion_u; // observacion que realizo el de administracion
    private $observacion_final; //ultima observacion realizada por el tecnico
    private $fecha_hora; //hora que se creo el ticket
    private $fecha_hora_final; //hora cuando termino el soporte
    private $tecnico;
    
    public function __construct($id_ticket=0, $estado=1, $tipo="", $cliente=null, $observacion_u="", $observacion_final="", $fecha_hora=null, $fecha_hora_final=null, $tecnico=null){
        $this->id_ticket = $id_ticket;
        $this->estado = $estado;
        $this->tipo = $tipo;
        $this->cliente = $cliente;
        $this->observacion_u = $observacion_u;
        $this->observacion_final = $observacion_final;
        $this->fecha_hora = $fecha_hora;
        $this->fecha_hora_final = $fecha_hora_final;
        $this->tecnico = $tecnico;
    }

    public function consultarTodos(){
        return "SELECT id_ticket, id_tipo_ticket, id_cliente, observacion_ticket, fecha_hora
                FROM ticket
                WHERE id_estado_ticket = ".$this->estado."
                ORDER BY fecha_hora ASC";
    }

    public function consultarPorId(){
        return "SELECT id_tipo_ticket, id_cliente, fecha_hora
                FROM ticket
                WHERE id_estado_ticket = ".$this->estado." AND id_ticket = ".$this->id_ticket."
                ";
    }

    public function consultarMenorQue(){
        return "SELECT id_ticket, id_tipo_ticket, id_cliente, observacion_ticket, fecha_hora
                FROM ticket
                WHERE id_estado_ticket = ".$this->estado." AND fecha_hora < '".$this->fecha_hora."'
                ORDER BY fecha_hora ASC
        ";
    }

    public function consultarMayorQue(){
        return "SELECT id_ticket, id_tipo_ticket, id_cliente, observacion_ticket, fecha_hora
                FROM ticket
                WHERE id_estado_ticket = ".$this->estado." AND fecha_hora > '".$this->fecha_hora."'
                ORDER BY fecha_hora ASC
        ";
    }

    public function consultarEntre($fechaLimite){
        return "SELECT id_ticket, id_tipo_ticket, id_cliente, observacion_ticket, fecha_hora
                FROM ticket
                WHERE id_estado_ticket = ".$this->estado." AND fecha_hora BETWEEN '".$fechaLimite."' AND '".$this->fecha_hora."'
                ORDER BY fecha_hora ASC
        ";
    }

}
?>