function createHandSVG(fingersUp, skinColor, outlineColor) {
    if (skinColor === undefined) skinColor = '#FDBF9F';
    if (outlineColor === undefined) outlineColor = '#E8A87C';
    const svgWidth = 100;
    const svgHeight = 130;
    const palmCX = 50;
    const palmCY = 65;
    const palmRX = 30;
    const palmRY = 35;
    const fingerDefs = [
        { bx: 24, by: 40, fw: 14, fhRaised: 44, fhLowered: 10, rx: 7 },
        { bx: 36, by: 28, fw: 12, fhRaised: 42, fhLowered: 8, rx: 6 },
        { bx: 46, by: 24, fw: 12, fhRaised: 46, fhLowered: 8, rx: 6 },
        { bx: 56, by: 26, fw: 12, fhRaised: 43, fhLowered: 8, rx: 6 },
        { bx: 66, by: 30, fw: 11, fhRaised: 39, fhLowered: 8, rx: 5 },
    ];
    let svg = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ' + svgWidth + ' ' + svgHeight + '" class="hand-svg">';
    svg += '<defs><filter id="softShadow"><feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.15"/></filter></defs>';
    svg += '<g filter="url(#softShadow)">';
    svg += '<ellipse cx="' + palmCX + '" cy="' + palmCY + '" rx="' + palmRX + '" ry="' + palmRY + '" fill="' + skinColor + '" stroke="' + outlineColor + '" stroke-width="2.5"/>';
    svg += '<rect x="' + (palmCX - 16) + '" y="' + (palmCY + palmRY - 8) + '" width="32" height="22" rx="10" fill="' + skinColor + '" stroke="' + outlineColor + '" stroke-width="2.5"/>';
    fingerDefs.forEach(function (fd, idx) {
        const isRaised = idx < fingersUp;
        const fh = isRaised ? fd.fhRaised : fd.fhLowered;
        const fy = fd.by - (isRaised ? fh : 2);
        const fingerFill = isRaised ? skinColor : '#E8C9B5';
        const fingerStroke = isRaised ? outlineColor : '#D4A88C';
        svg += '<rect x="' + fd.bx + '" y="' + fy + '" width="' + fd.fw + '" height="' + fh + '" rx="' + fd.rx + '" fill="' + fingerFill + '" stroke="' + fingerStroke + '" stroke-width="2"/>';
        if (isRaised && fh > 15) {
            svg += '<ellipse cx="' + (fd.bx + fd.fw / 2) + '" cy="' + (fy + 6) + '" rx="' + (fd.fw / 2.5) + '" ry="4" fill="rgba(255,255,255,0.35)"/>';
        }
    });
    svg += '<line x1="' + (palmCX - 10) + '" y1="' + (palmCY - 2) + '" x2="' + (palmCX + 12) + '" y2="' + (palmCY - 2) + '" stroke="' + outlineColor + '" stroke-width="1" opacity="0.5"/>';
    svg += '<line x1="' + (palmCX - 8) + '" y1="' + (palmCY + 8) + '" x2="' + (palmCX + 8) + '" y2="' + (palmCY + 8) + '" stroke="' + outlineColor + '" stroke-width="1" opacity="0.4"/>';
    svg += '</g></svg>';
    return svg;
}

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
        html += createHandSVG(h.fingersUp);
    });
    html += '</div>';
    return html;
}
