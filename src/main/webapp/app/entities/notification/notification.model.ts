import { BaseEntity } from './../../shared';

export const enum Channel {
    'EMAIL',
    'SMS',
    'MOBILE'
}

export class Notification implements BaseEntity {
    constructor(
        public id?: number,
        public channel?: Channel,
        public to?: string,
        public from?: string,
        public msgUri?: string,
    ) {
    }
}
