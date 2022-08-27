const GAME_OBJECT = [];

export class GameObject {
    constructor() {
        GAME_OBJECT.push(this);
        this.timedelta = 0;
        this.has_called_start = false;
    }

    start() {       //只执行一次

    }

    update() {      //每一帧执行一次，除了第一帧外

    }

    on_destroy() {      //删除之前执行

    }

    destroy() {
        this.on_destroy();

        for (let i in GAME_OBJECT) {
            const obj = GAME_OBJECT[i];
            if (obj == this) {
                GAME_OBJECT.splice(i);
                break;
            }
        }
    }
}

let last_timestamp;
const step = timedelta => {
    for (let obj of GAME_OBJECT) {
        if (!obj.has_called_start) {
            obj.has_called_start = true;
            obj.start();
        }else {
            obj.timedelta = timedelta - last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timedelta;
    requestAnimationFrame(step)
}

requestAnimationFrame(step)