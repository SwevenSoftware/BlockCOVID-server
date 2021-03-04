import React, { Component, createRef, RefObject } from "react";
import Room, { Desk } from "./Room";
import "./styles.css";

interface Pos2d {
  x: number;
  y: number;
}

interface Settings {
  radius: number;
  dist: Pos2d;
  width: number;
  height: number;
}

class DotGrid extends Component<{ width: number }, { height: number }> {
  canvasRef: RefObject<HTMLCanvasElement>;
  gridSettings: Settings;
  mousePos: Pos2d;
  selection: {
    started: boolean;
    ended: boolean;
    posStart: Pos2d;
    posEnd: Pos2d;
  };
  room: Room;
  constructor(props) {
    super(props);
    this.canvasRef = createRef<HTMLCanvasElement>();
    this.handleMouseMove = this.handleMouseMove.bind(this);
    this.checkBox = this.checkBox.bind(this);

    this.mousePos = { x: -10, y: -10 };
    this.selection = {
      started: false,
      ended: false,
      posStart: { x: 0, y: 0 },
      posEnd: { x: 0, y: 0 }
    };
    this.gridSettings = {
      radius: 2,
      dist: {
        x: 0,
        y: 0,
      },
      width: props.width,
      height: props.height
    };

    this.room = new Room(20, 20);
  }

  public setSize(width: number, height: number) {
    this.canvasRef.current?.setAttribute("width", width.toString());
    this.canvasRef.current?.setAttribute("height", width.toString());
  }

  componentDidMount() {
    setTimeout(() => this.updateCanvas(), 100);
  }

  handleMouseMove(e: React.MouseEvent<HTMLCanvasElement, MouseEvent>) {
    if (this.mousePos.x !== e.clientX || this.mousePos.y !== e.clientY) {
      this.mousePos = {
        x: e.clientX,
        y: e.clientY
      };
      this.updateCanvas();
    }
  }

  checkBox(e: React.MouseEvent<HTMLCanvasElement, MouseEvent>) {
    const boundRect: DOMRect | undefined = this.canvasRef.current?.getBoundingClientRect();
    if(!boundRect || (e.clientX-boundRect.x)/boundRect.width >= 1-1/this.room.cells.x || 
    (e.clientY-boundRect.y)/boundRect.height >= 1-1/this.room.cells.y ) return;
    const pointPos: Pos2d = {
      x:
        Math.floor((e.clientX - boundRect.x) / this.gridSettings.dist.x) *
          this.gridSettings.dist.x +
        this.gridSettings.dist.x / 2 +
        this.gridSettings.radius,
      y:
        Math.floor((e.clientY - boundRect.y) / this.gridSettings.dist.y) *
          this.gridSettings.dist.y +
        this.gridSettings.dist.y / 2 +
        this.gridSettings.radius
    };
    const id: number | null = this.room.addDesk(pointPos.x, pointPos.y);
    if (id && pointPos.x < 100 && pointPos.y < 100) this.room.setInUse(id, true); // debug
    this.updateCanvas();
  }

  checkDistance(mouse: number, point: number, step: number, dim: number): boolean {
    if(mouse/dim >= 1 - step/dim || mouse < point)
      return false;
    return mouse - point < step;
  }

  private static drawDesk(
    ctx: CanvasRenderingContext2D,
    x: number,
    y: number,
    radius: number,
    inUse: boolean
  ) {
    ctx.beginPath();
    ctx.arc(x, y, radius * 1.2, 0, Math.PI * 2);
    ctx.fillStyle = "lightgrey";
    ctx.fill();
    ctx.closePath();
    ctx.beginPath();
    ctx.arc(x, y, radius, 0, Math.PI * 2);
    if (inUse) ctx.fillStyle = "orange";
    else ctx.fillStyle = "green";
    ctx.fill();
    ctx.closePath();
  }

  public resetView() {
    this.room.clearDesks();
    this.updateCanvas();
  }

  updateCanvas() {
    const canvas: HTMLCanvasElement | null = this.canvasRef.current;
    if(!canvas) return;
    const canvasRect: DOMRect = canvas.getBoundingClientRect();
    const ctx: CanvasRenderingContext2D | undefined | null = this.canvasRef.current?.getContext(
      "2d"
    );
    if(!ctx) return;
    this.gridSettings.dist = {
      x: canvas.width / this.room.cells.x,
      y: canvas.height / this.room.cells.y
    };
    const dist: Pos2d = this.gridSettings.dist;
    const radius: number = this.gridSettings.radius;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    for (var x: number = radius; x < canvas.width - radius; x += dist.x) {
      for (var y: number = radius; y < canvas.height - radius; y += dist.y) {
        ctx.beginPath();
        ctx.arc(x, y, radius, 0, 2 * Math.PI);
        ctx.fillStyle = "grey";
        ctx.fill();
        ctx.closePath();

        if (
          this.checkDistance(this.mousePos.x - canvasRect.x, x, dist.x, canvasRect.width) &&
          this.checkDistance(this.mousePos.y - canvasRect.y, y, dist.y, canvasRect.height)
        ) {
          ctx.beginPath();
          ctx.fillStyle = "#f005";
          ctx.fillRect(x, y, dist.x, dist.y);
          ctx.closePath();
        }
      }
    }

    // desks
    const ids: Array<number> = this.room.getIds();
    for (var i in ids) {
      const pos: Pos2d | null | undefined = this.room.getPosition(ids[i]);
      const inUse: boolean | null | undefined = this.room.isInUse(ids[i]);
      if(pos && inUse != undefined && inUse != null)
      DotGrid.drawDesk(
        ctx,
        pos.x,
        pos.y,
        radius * 4,
        inUse,
      );
    }
  }

  render() {
    return (
      <canvas
        ref={this.canvasRef}
        onMouseMove={this.handleMouseMove}
        onClick={this.checkBox}
        width={this.gridSettings.width}
        height={this.gridSettings.height}
      />
    );
  }
}

export default DotGrid;
