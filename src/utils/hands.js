function getHandsForNumber(num) {
    if (num <= 0) return [{ fingersUp: 0 }];
    if (num <= 5) return [{ fingersUp: num }];
    const hands = [{ fingersUp: 5 }];
    const rem = num - 5;
    if (rem > 0) hands.push({ fingersUp: rem });
    return hands;
}

export function renderHandsForNumber(num) {
    const hands = getHandsForNumber(num);
    let html = '<div class="hand-group">';
    hands.forEach(function (h) {
        html += '<img class="hand-svg" src="/assets/images/hand-' + h.fingersUp + '.svg" alt="' + h.fingersUp + ' finger' + (h.fingersUp !== 1 ? 's' : '') + '">';
    });
    html += '</div>';
    return html;
}
