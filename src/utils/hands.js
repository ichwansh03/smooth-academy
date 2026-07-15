export function renderHandsForNumber(num) {
    if (num >= 0 && num <= 9) {
        return '<img class="hand-svg" src="/assets/images/' + num + '.svg" alt="' + num + '">';
    }
    return '<span class="number-text">' + num + '</span>';
}
