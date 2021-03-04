interface Pos2d {
  x: number;
  y: number;
}

class Desk {
  pos: Pos2d;
  id: number;
  inUse: boolean;
  constructor(x: number, y: number, id: number) {
    this.pos = {
      x: x,
      y: y
    };
    this.id = id;
    this.inUse = false;
  }
}

class Room {
  private desks: Map<number, Desk>;
  public cells: Pos2d;
  constructor(cellsX: number, cellsY: number) {
    this.desks = new Map<number, Desk>();
    this.cells = { x: cellsX, y: cellsY };
  }

  public searchByPos(x: number, y: number): number | null {
    for (let id of this.getIds()) {
      if (this.getPosition(id)?.x === x && this.getPosition(id)?.y === y)
        return id;
    }
    return null;
  }

  public getPosition(id: number): Pos2d | null | undefined {
    if (this.desks.has(id)) return this.desks.get(id)?.pos;
    return null;
  }

  public addDesk(x: number, y: number): number | null {
    if (this.searchByPos(x, y) === null) {
      const nextId: number =
        this.getIds().length > 0 ? Math.max(...this.getIds()) + 1 : 0;
      this.desks.set(nextId, new Desk(x, y, nextId));
      return nextId;
    }
    return null;
  }

  public removeDesk(id: number): void {
    delete this.desks[id];
  }

  public setInUse(id: number, inUse: boolean = true): void {
    var getter: Desk | undefined = this.desks.get(id);
    if (getter) getter.inUse = inUse;
  }

  public isInUse(id: number): boolean | null | undefined {
    if (this.desks.has(id)) return this.desks.get(id)?.inUse;
    return null;
  }

  public getIds(): Array<number> {
    return Array.from(this.desks.keys());
  }

  public clearDesks() {
    this.desks.clear();
  }
}

export default Room;
export { Desk };
