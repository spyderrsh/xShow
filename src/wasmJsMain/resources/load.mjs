import { instantiate } from './xshow.uninstantiated.mjs';

await wasmSetup;

instantiate({ skia: Module['asm'] });
