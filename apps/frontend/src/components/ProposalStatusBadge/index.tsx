import { PROPOSAL_STATUS_LABEL, PROPOSAL_STATUS_STYLE } from "@/lib/proposal-status";
import type { ProposalStatus } from "@/lib/proposals";

export function ProposalStatusBadge({ status }: { status: ProposalStatus }) {
  return (
    <span
      className={`shrink-0 rounded px-1.5 py-0.5 text-[9px] font-semibold ${PROPOSAL_STATUS_STYLE[status]}`}
    >
      {PROPOSAL_STATUS_LABEL[status]}
    </span>
  );
}
